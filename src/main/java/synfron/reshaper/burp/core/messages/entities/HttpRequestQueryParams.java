package synfron.reshaper.burp.core.messages.entities;

import lombok.Getter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import synfron.reshaper.burp.core.utils.*;

import java.util.List;

public class HttpRequestQueryParams extends HttpEntity {
    @Getter
    private boolean changed;
    private final List<NameValuePair> parameters;
    private ListMap<String, String> params;

    public HttpRequestQueryParams(List<NameValuePair> parameters) {
        this.parameters = parameters;
    }

    public void prepare() {
        if (params == null) {
            params = new ListMap<>();
            for (NameValuePair parameter : parameters) {
                params.add(parameter.getName(), parameter.getValue());
            }
        }
    }

    public boolean hasQueryParameter(String name) {
        prepare();
        return params.containsKey(name);
    }

    public String getQueryParameter(String name, GetItemPlacement itemPlacement) {
        prepare();
        return params.get(name, itemPlacement);
    }

    public void setQueryParameter(String name, String value, SetItemPlacement itemPlacement) {
        if (value != null) {
            prepare();
            params.set(name, value, itemPlacement);
            changed = true;
        } else {
            deleteParameter(name, IItemPlacement.toDelete(itemPlacement));
        }
    }

    public void deleteParameter(String name, DeleteItemPlacement itemPlacement) {
        prepare();
        params.remove(name, itemPlacement);
        changed = true;
    }

    public List<NameValuePair> getValue() {
        return !isChanged() ? parameters : params.entries(BasicNameValuePair::new);
    }
}
