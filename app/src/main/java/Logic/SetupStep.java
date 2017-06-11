package Logic;

/**
 * Created by Le Pham Minh Duc on 6/11/2017.
 */

public enum SetupStep {
    //the values
    Not_Initialized(-1),
    Reading_Instruction(0),
    Right_Hand_Down(1),
    Right_Hand_Front(2),
    Right_Hand_Up(3),
    Left_Hand_Down(4),
    Left_Hand_Front(5),
    Left_Hand_Up(6),
    Finish(100);

    //necessary function, enum in Java is a bit sillier than in C#.
    private final int setupCode;

    SetupStep(int code) { this.setupCode = code; }

    public int value() { return setupCode; }
}
