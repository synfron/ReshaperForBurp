package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import burp.BurpExtender;
import burp.ICookie;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.utils.Select;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.util.*;
import java.util.stream.Collectors;

public class CookieJarVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private final Select<String> domains;
    @Getter
    private Select<String> names;
    @Getter
    private Select<String> paths;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public CookieJarVariableTagWizardModel() {
        List<String> domains = BurpExtender.getCallbacks().getCookieJarContents().stream()
                .map(ICookie::getDomain)
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
        List<String> names = BurpExtender.getCallbacks().getCookieJarContents().stream()
                .filter(cookie -> cookie.getDomain().equals(this.domains.getSelectedOption()))
                .map(ICookie::getName)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        this.names = new Select<>(names, names.stream().findFirst().orElse(""));
        propertyChanged("names", names);
        resetPaths();
    }

    private void resetPaths() {
        List<String> paths = BurpExtender.getCallbacks().getCookieJarContents().stream()
                .filter(cookie -> cookie.getDomain().equals(this.domains.getSelectedOption()) && cookie.getDomain().equals(this.names.getSelectedOption()))
                .map(ICookie::getPath)
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
