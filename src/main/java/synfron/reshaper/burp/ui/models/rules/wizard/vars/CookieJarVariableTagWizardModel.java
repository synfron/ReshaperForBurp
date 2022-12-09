package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import burp.BurpExtender;
import burp.api.montoya.http.message.cookies.Cookie;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.utils.Select;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CookieJarVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private final Select<String> domains;
    private final List<Cookie> cookies;
    @Getter
    private Select<String> names;
    @Getter
    private Select<String> paths;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public CookieJarVariableTagWizardModel() {
        this.cookies = BurpExtender.getApi() != null ? BurpExtender.getApi().http().cookieJar().cookies() : Collections.emptyList();
        List<String> domains = this.cookies.stream()
                .map(Cookie::domain)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        this.domains = new Select<>(domains, domains.stream().findFirst().orElse(""));
        resetNames();
        resetPaths();
    }

    public CookieJarVariableTagWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    private void resetNames() {
        List<String> names = this.cookies.stream()
                .filter(cookie -> cookie.domain().equals(this.domains.getSelectedOption()))
                .map(Cookie::name)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        this.names = new Select<>(names, names.stream().findFirst().orElse(""));
        propertyChanged("names", names);
        resetPaths();
    }

    private void resetPaths() {
        List<String> paths = this.cookies.stream()
                .filter(cookie -> cookie.domain().equals(this.domains.getSelectedOption()) && cookie.domain().equals(this.names.getSelectedOption()))
                .map(Cookie::path)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        this.paths = new Select<>(paths, paths.stream().findFirst().orElse(""));
        propertyChanged("paths", paths);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setDomain(String domain) {
        domains.setSelectedOption(domain);
        resetNames();
        propertyChanged("domain", domains);
    }

    public void setName(String name) {
        names.setSelectedOption(name);
        resetPaths();
        propertyChanged("name", names);
    }

    public void setPath(String path) {
        paths.setSelectedOption(path);
        propertyChanged("path", paths);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(domains.getSelectedOption())) {
            errors.add("Domain is required");
        }
        if (StringUtils.isEmpty(names.getSelectedOption())) {
            errors.add("Name is required");
        }
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.CookieJar;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(
                        VariableSource.CookieJar,
                        getTagSafe(domains.getSelectedOption()),
                        getTagSafe(names.getSelectedOption()),
                        StringUtils.defaultIfEmpty(paths.getSelectedOption(), null)
                ) :
                null;
    }

    private String getTagSafe(String value) {
        return value.contains(":") ? "\"" + StringUtils.strip(value, "\"").replace("\"", "\\\"") + "\"" : value;
    }
}
