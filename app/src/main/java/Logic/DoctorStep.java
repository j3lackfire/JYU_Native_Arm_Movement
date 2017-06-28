package Logic;

/**
 * Created by Le Pham Minh Duc on 6/28/2017.
 */

public enum DoctorStep {
    //the values
    Not_Initialized(-1),
    Reading_Instruction(0),
    Right_Hand_Down(1),//hold the hand at the down position
    Right_Hand_Down_To_Front(2), //on the way of movement from down to front
    Right_Hand_Front(3), //hold at the front for 5 seconds
    Right_Hand_Front_To_Up(4),//on the way from front to up
    Right_Hand_Up(5), //hold at the up position for 5 seconds
    Left_Hand_Down(6),
    Left_Hand_Down_To_Front(7),
    Left_Hand_Front(8),
    Left_Hand_Front_To_Up(9),
    Left_Hand_Up(10),
    Finish(100);

    //necessary function, enum in Java is a bit sillier than in C#.
    private final int doctorStepCode;

    DoctorStep(int code) { this.doctorStepCode = code; }

    public int value() { return doctorStepCode; }
}
