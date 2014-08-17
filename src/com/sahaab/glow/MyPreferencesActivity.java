package com.sahaab.glow;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import net.margaritov.preference.colorpicker.ColorPickerView;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MyPreferencesActivity extends PreferenceActivity {
	 
	SharedPreferences prefs;
	 
    @Override
 
    protected void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.prefs);
        
        Preference xPreference = getPreferenceScreen().findPreference("speedx");
        
        // add the validator
        xPreference.setOnPreferenceChangeListener(numberCheckListener);
 
        Preference yPreference = getPreferenceScreen().findPreference("speedy");
        
        // add the validator
        yPreference.setOnPreferenceChangeListener(numberCheckListener);      
        
        Preference defaultreset = getPreferenceScreen().findPreference("reset");
        
        defaultreset.setOnPreferenceClickListener(resetdefault);  
       
     	

        
    }
 
    
    /**
       * Checks that a preference is a valid numerical value
       */

      Preference.OnPreferenceChangeListener numberCheckListener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          // check that the string is an integer
          if (newValue != null && newValue.toString().length() > 0
              && newValue.toString().matches("\\d*")) {
            return true;
          }
          // If now create a message to the user
          Toast.makeText(MyPreferencesActivity.this, "Invalid Input",
              Toast.LENGTH_SHORT).show();
          return false;
        }
      }; 
      Preference.OnPreferenceClickListener resetdefault = new OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
        	CheckBoxPreference touch = (CheckBoxPreference)findPreference("touch");
        	
        	 touch.setChecked(true);
        	 
          	ColorPickerPreference color1 = (ColorPickerPreference)findPreference("color1");
         	
          	color1.onColorChanged(Color.rgb(0x00, 0x97, 0xa7));
         	
         	ColorPickerPreference color2 = (ColorPickerPreference)findPreference("color2");
         	
         	color2.onColorChanged(Color.rgb(0x42, 0xec, 0xff));
         	
         	
        	return false;
        }
      };        
      
 
}
