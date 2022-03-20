package synfron.reshaper.burp.core.rules.thens.entities.evaluate;

import lombok.Getter;

public enum Operation {
    Add("Add", 2),
    Subtract("Subtract", 2),
    Multiply("Multiply", 2),
    DivideBy("Divide By", 2),
    Increment("Increment", 1),
    Decrement("Decrement", 1),
    Mod("Mod", 2),
    Abs("Abs", 1),
    Round("Round", 1),
    Equals("Equals", 2),
    GreaterThan("Greater Than", 2),
    GreaterThanOrEquals("Greater Than Or Equals", 2),
    LessThan("Less Than", 2),
    LessThanOrEquals("Less Than Or Equals", 2);

    @Getter
    private final String name;
    @Getter
    private final int inputs;

    Operation(String name, int inputs) {
        this.name = name;
        this.inputs = inputs;
    }

    @Override
    public String toString() {
        return name;
    }


}
