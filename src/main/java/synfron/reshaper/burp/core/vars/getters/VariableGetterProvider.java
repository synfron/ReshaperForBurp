package synfron.reshaper.burp.core.vars.getters;

import synfron.reshaper.burp.core.vars.VariableSource;

public class VariableGetterProvider {
    private static final AccessorVariableGetter accessorVariableOperator = new AccessorVariableGetter();
    private static final CustomVariableGetter customVariableOperator = new CustomVariableGetter();
    private static final CustomListVariableGetter customListVariableOperator = new CustomListVariableGetter();

    public static VariableGetter get(VariableSource variableSource) {
        if (variableSource.isAccessor()) {
            return accessorVariableOperator;
        }
        else if (variableSource.isList()) {
            return customListVariableOperator;
        }
        return customVariableOperator;
    }
}
