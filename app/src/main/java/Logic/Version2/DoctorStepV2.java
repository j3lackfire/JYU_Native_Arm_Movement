package Logic.Version2;

/**
 * Created by Le Pham Minh Duc on 7/7/2017.
 */

public enum DoctorStepV2 {
    //the values
    Not_Initialized(-1),
    Reading_Instruction(0),
    Calibration (1), //calibration, put the phone on the table for a few seconds just to be sure.
    Right_Hand_Down(2),//hold the hand at the down position
    Right_Hand_Front(3), //hold at the front for 5 seconds
    Right_Hand_Up(4), //hold at the up position for 5 seconds
    Left_Hand_Down(5),
    Left_Hand_Front(6),
    Left_Hand_Up(7),
    Finish(100);

    //necessary function, enum in Java is a bit sillier than in C#.
    private final int doctorStepCode;

    DoctorStepV2(int code) { this.doctorStepCode = code; }

    public int value() { return doctorStepCode; }
}
