package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.os.Bundle;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferencesFix(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
