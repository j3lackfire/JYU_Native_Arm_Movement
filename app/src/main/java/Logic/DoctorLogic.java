package Logic;

/**
 * Created by Le Pham Minh Duc on 6/28/2017.
 */

public class DoctorLogic {

    //singleton
    private static DoctorLogic instance;
    public static DoctorLogic getInstance() {
        if (instance == null) {
            instance = new DoctorLogic();
        }
        return instance;
    }

    private DoctorStep currentDoctorStep = DoctorStep.Not_Initialized;

    private DoctorLogic() {
        prepareDoctorLogic();
    }

    public void prepareDoctorLogic() {
        currentDoctorStep = DoctorStep.Reading_Instruction;
    }

    public void toNextStep() { currentDoctorStep = getNextDoctorStep(); }

    public DoctorStep getCurrentDoctorStep() { return currentDoctorStep; }

    private DoctorStep getNextDoctorStep() {
        switch (currentDoctorStep) {
            case Reading_Instruction:
                return DoctorStep.Right_Hand_Down;
            case Right_Hand_Down:
                return DoctorStep.Right_Hand_Down_To_Front;
            case Right_Hand_Down_To_Front:
                return DoctorStep.Right_Hand_Front;
            case Right_Hand_Front:
                return DoctorStep.Right_Hand_Front_To_Up;
            case Right_Hand_Front_To_Up:
                return DoctorStep.Right_Hand_Up;
            case Right_Hand_Up:
                return DoctorStep.Left_Hand_Down;
            case Left_Hand_Down:
                return DoctorStep.Left_Hand_Down_To_Front;
            case Left_Hand_Down_To_Front:
                return DoctorStep.Left_Hand_Front;
            case Left_Hand_Front:
                return DoctorStep.Left_Hand_Front_To_Up;
            case Left_Hand_Front_To_Up:
                return DoctorStep.Left_Hand_Up;

            case Not_Initialized:
            case Left_Hand_Up:
            case Finish:
            default:
                return DoctorStep.Finish;
        }
    }

    public boolean isTrackingMotion () {
        switch (currentDoctorStep) {
            case Not_Initialized:
            case Reading_Instruction:
            case Finish:
                return false;

            default:
                return true;
        }
    }

    public String getSetupKey(DoctorStep step) {
        switch (step) {
            default:
                return "Invalid";

            case Right_Hand_Down:
                return SetupLogic.right_hand_down_position;
            case Right_Hand_Down_To_Front:
            case Right_Hand_Front:
                return SetupLogic.right_hand_front_position;
            case Right_Hand_Front_To_Up:
            case Right_Hand_Up:
                return SetupLogic.right_hand_up_position;
            case Left_Hand_Down:
                return SetupLogic.left_hand_down_position;
            case Left_Hand_Down_To_Front:
            case Left_Hand_Front:
                return SetupLogic.left_hand_front_position;
            case Left_Hand_Front_To_Up:
            case Left_Hand_Up:
                return SetupLogic.left_hand_up_position;
        }
    }
}
