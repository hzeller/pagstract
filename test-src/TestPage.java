import org.pagstract.PageModel;
import org.pagstract.model.ActionModel;
import org.pagstract.model.ComboboxModel;
import org.pagstract.model.TextFieldModel;
import org.pagstract.view.namespace.Namespace;

public interface TestPage extends PageModel {
    void setStringValue(String stringValue);
    void setListValue(FooBean[] list);
    void setActionValue(ActionModel model);
}
