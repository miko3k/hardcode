package interactive;

import org.deletethis.hardcode.HardcodeRoot;

@HardcodeRoot
public class ChildData {
    private String value;
    private ChildData more;

    public ChildData(String value) {
        this.value = value;
    }

    public ChildData(String value, ChildData more) {
        this.value = value;
        this.more = more;
    }
}
