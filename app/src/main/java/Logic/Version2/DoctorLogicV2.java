package Logic.Version2;

/**
 * Created by Le Pham Minh Duc on 7/7/2017.
 */

public class DoctorLogicV2 {


    private String instructionText_reading_instruction =
            "Welcome to the stroke arm test application.\n" +
                    "Please read these instructions carefully.\n" +
                    "You will be asked to perform some actions to calibrate your data.\n" +
                    "Move your phone to the requested position and keep the phone still for 5 seconds\n" +
                    "Press START to start the setup, press CANCEL to go back to the main screen.";
    private String instructionText_right_hand_down =
            "Hold the phone in your RIGHT hand and move it \n" +
                    "to the lowest possible position of your body";
    private String instructionText_right_hand_front =
            "Hold the phone in your RIGHT hand and move it \n" +
                    "to the front of your body";
    private String instructionText_right_hand_up =
            "Hold the phone in your RIGHT hand and move it \n" +
                    "to the highest possible position of your body above your head";
    private String instructionText_left_hand_down =
            "Hold the phone in your LEFT hand and move it \n" +
                    "to the lowest possible position of your body";
    private String instructionText_left_hand_front =
            "Hold the phone in your LEFT hand and move it \n" +
                    "to the front of your body";
    private String instructionText_left_hand_up =
            "Hold the phone in your LEFT hand and move it \n" +
                    "to the highest possible position of your body above your head";
    private String instructionText_finish =
            "Set up completed, all your data have been saved\n" +
                    "Thanks for your time. :)";


    //singleton
    private static DoctorLogicV2 instance;
    public static DoctorLogicV2 getInstance() {
        if (instance == null) {
            instance = new DoctorLogicV2();
        }
        return instance;
    }

    private DoctorStepV2 currentDoctorStep = DoctorStepV2.Not_Initialized;

    public DoctorLogicV2() { prepareDoctorLogic(); }

    public void prepareDoctorLogic() {
        currentDoctorStep = DoctorStepV2.Reading_Instruction;
    }

    public void toNextStep() {
        currentDoctorStep = getNextDoctorStep();
    }

    public DoctorStepV2 getCurrentDoctorStep() {return currentDoctorStep;}

    public DoctorStepV2 getNextDoctorStep() {
        switch (currentDoctorStep) {
            case Reading_Instruction:
                return DoctorStepV2.Calibration;
            case Calibration:
                return DoctorStepV2.Right_Hand_Down;
            case Right_Hand_Down:
                return DoctorStepV2.Right_Hand_Front;
            case Right_Hand_Front:
                return DoctorStepV2.Right_Hand_Up;
            case Right_Hand_Up:
                return DoctorStepV2.Left_Hand_Down;
            case Left_Hand_Down:
                return DoctorStepV2.Left_Hand_Front;
            case Left_Hand_Front:
                return DoctorStepV2.Left_Hand_Up;

            case Not_Initialized:
            case Left_Hand_Up:
            case Finish:
            default:
                return DoctorStepV2.Finish;
        }
    }

    //If the user is still reading the instruction, then no need to do anything.
    public boolean isTrackingMotion() {
        switch (currentDoctorStep) {
            case Not_Initialized:
            case Calibration:
            case Reading_Instruction:
            case Finish:
                return false;

            default:
                return true;
        }
    }

    public String getInstructionText() {
        switch (currentDoctorStep) {
            case Not_Initialized:
                return "The setup is not initialized, something must be wrong. Please contact developer!!!";
            case Reading_Instruction:
                return instructionText_reading_instruction;
            case Right_Hand_Down:
                return instructionText_right_hand_down;
            case Right_Hand_Front:
                return instructionText_right_hand_front;
            case Right_Hand_Up:
                return instructionText_right_hand_up;
            case Left_Hand_Down:
                return instructionText_left_hand_down;
            case Left_Hand_Front:
                return instructionText_left_hand_front;
            case Left_Hand_Up:
                return instructionText_left_hand_up;
            case Finish:
                return instructionText_finish;
            default:
                return "Doctor step v2 not defined !!!!, please contact developer !!!";
        }
    }
}
