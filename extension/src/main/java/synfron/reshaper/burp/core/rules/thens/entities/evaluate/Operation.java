package synfron.reshaper.burp.core.rules.thens.entities.evaluate;

import lombok.Getter;

public enum Operation {
    Add("Add", 2, true),
    Subtract("Subtract", 2, true),
    Multiply("Multiply", 2, true),
    DivideBy("Divide By", 2, true),
    Increment("Increment", 1, true),
    Decrement("Decrement", 1, true),
    Mod("Mod", 2, true),
    Abs("Abs", 1, true),
    Round("Round", 1, true),
    Not("Not", 1, false, 0),
    Equals("Equals", 2, false, 0),
    NotEquals("Not Equals", 2, false, 0),
    Contains("Contains", 2, false, 0),
    GreaterThan("Greater Than", 2, true),
    GreaterThanOrEquals("Greater Than Or Equals", 2, true),
    LessThan("Less Than", 2, true),
    LessThanOrEquals("Less Than Or Equals", 2, true);

    @Getter
    private final String name;
    @Getter
    private final int inputs;
    @Getter
    private final boolean numeric;
    @Getter
    private final int minInputs;

    Operation(String name, int inputs, boolean numeric) {
        this(name, inputs, numeric, inputs);
    }

    Operation(String name, int inputs, boolean numeric, int minInputs) {
        this.name = name;
        this.inputs = inputs;
        this.numeric = numeric;
        this.minInputs = minInputs;
    }

    @Override
    public String toString() {
        return name;
    }


}
