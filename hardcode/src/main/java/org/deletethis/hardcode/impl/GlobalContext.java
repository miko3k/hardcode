package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.objects.Expression;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

class GlobalContext {
    private final Integer maxClassLines;
    private final String mainClassName;
    private final NumberNameAllocator classNameAllocator;
    private final ClassContext mainClassContext;
    private final List<ClassContext> classContexts = new ArrayList<>();

    GlobalContext(Integer maxClassLines, String mainClassName) {
        this.maxClassLines = maxClassLines;
        this.mainClassName = mainClassName;
        this.classNameAllocator = new NumberNameAllocator();
        this.classNameAllocator.newName(mainClassName);
        this.mainClassContext = new ClassContext(this, mainClassName);
        this.classContexts.add(mainClassContext);
    }

    ClassContext getMainClassContext() {
        return mainClassContext;
    }

    ClassContext getCurrentClassContext() {
        return classContexts.get(classContexts.size()-1);
    }

    ClassContext createAuxiliaryContext() {
        ClassContext cc = new ClassContext(this, classNameAllocator.newName(mainClassName));
        classContexts.add(cc);
        return cc;
    }

    Integer getMaxClassLines() { return maxClassLines; }

    private MethodSpec.Builder createMethod(MethodContext methodContext) {
        Set<Class<? extends Throwable>> unhandledExceptions = methodContext.getUnhandledExceptions();
        String exceptionVariable = methodContext.allocateVariable(Exception.class);

        MethodSpec.Builder bld = MethodSpec.methodBuilder(methodContext.getMethodName());

        bld.returns(methodContext.getReturnType());
        bld.addParameters(methodContext.getParameters());

        if(!unhandledExceptions.isEmpty()) {
            bld.beginControlFlow("try");
        }

        bld.addCode(methodContext.getCode().build());

        if(!unhandledExceptions.isEmpty()) {
            // we are not using multi catch here - it would be more difficult and would make code require java 1.7
            for(Class<?> c: unhandledExceptions) {
                bld.nextControlFlow("catch($T $L)", c, exceptionVariable);
                bld.addStatement("throw new $T($L)", IllegalStateException.class, exceptionVariable);
            }
            bld.endControlFlow();
        }

        AnnotationSpec.Builder ann = AnnotationSpec.builder(SuppressWarnings.class);
        ann.addMember("value", "$S", "unchecked");
        bld.addAnnotation(ann.build());


        return bld;
    }

    List<TypeSpec> buildAll(MethodContext mainMethod, boolean supplier) {
        boolean mainClass = true;
        List<TypeSpec> result = new ArrayList<>(classContexts.size());
        for(ClassContext classContext: classContexts) {
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(classContext.getClassName());
            if(mainClass) {
                typeBuilder.addModifiers(Modifier.PUBLIC);
            } else {
                typeBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
            }

            for(MethodContext methodContext: classContext.getMethods()) {
                MethodSpec.Builder methodBuilder = createMethod(methodContext);

                if(methodContext == mainMethod) {
                    assert mainClass;

                    methodBuilder.addModifiers(Modifier.PUBLIC);

                    if(supplier) {
                        typeBuilder.addSuperinterface(ParameterizedTypeName.get(Supplier.class, mainMethod.getReturnType()));
                        methodBuilder.addAnnotation(Override.class);
                    }
                } else {
                    methodBuilder.addModifiers(Modifier.STATIC);
                    if(methodContext.getAccessModifier() == AccessModifier.PRIVATE)
                        methodBuilder.addModifiers(Modifier.PRIVATE);
                }
                typeBuilder.addMethod(methodBuilder.build());
            }

            mainClass = false;
            result.add(typeBuilder.build());
        }
        return result;
    }
}
