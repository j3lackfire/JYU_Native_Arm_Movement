package Logic;

import android.content.SharedPreferences;

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

    private SavedValue[] previouslySavedValue = new SavedValue[6];

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

    public void prepareAllSavedValue(SharedPreferences sharedPref) {
        String jsonString;
        for (int i = 0; i < 6; i ++) {
            jsonString = sharedPref.getString(getSetupKeyFromIndex(i), "-");
            if (jsonString.equals("-")) {
                previouslySavedValue[i] = new SavedValue();
            } else {
                previouslySavedValue[i] = SavedValue.fromJson(jsonString);
            }
        }
    }

    //used for the function above
    private String getSetupKeyFromIndex(int index) {
        switch (index) {
            case 0:
                return SetupLogic.right_hand_down_position;
            case 1:
                return SetupLogic.right_hand_front_position;
            case 2:
                return SetupLogic.right_hand_up_position;
            case 3:
                return SetupLogic.left_hand_down_position;
            case 4:
                return SetupLogic.left_hand_front_position;
            case 5:
                return SetupLogic.left_hand_up_position;
            default:
                return "Invalid";
        }
    }

    public SavedValue getCurrentSavedValue() {
        switch (currentDoctorStep) {
            default:
                return new SavedValue();

            case Right_Hand_Down:
                return previouslySavedValue[0];
            case Right_Hand_Down_To_Front:
            case Right_Hand_Front:
                return previouslySavedValue[1];
            case Right_Hand_Front_To_Up:
            case Right_Hand_Up:
                return previouslySavedValue[2];
            case Left_Hand_Down:
                return previouslySavedValue[3];
            case Left_Hand_Down_To_Front:
            case Left_Hand_Front:
                return previouslySavedValue[4];
            case Left_Hand_Front_To_Up:
            case Left_Hand_Up:
                return previouslySavedValue[5];
        }
    }

    ///if the phone is moving, check if it reaches the destined position
    public boolean shouldTrackPosition() {
        switch (currentDoctorStep) {
            case Right_Hand_Down_To_Front:
            case Right_Hand_Front_To_Up:
            case Left_Hand_Down_To_Front:
            case Left_Hand_Front_To_Up:
                return true;
        }
        return false;
    }
}
