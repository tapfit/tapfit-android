package co.tapfit.android.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/*
777IDDDDDDDDDDDDD777777777777777778DDDDDDDO777777777777O8888887777777777O88888$777777777788888877777777777O88888D8I77777777777778DDDDDDDDD7I77777777777777777777
77777IDDDDDDDDDDDD$7777777777777777ZDD8888D87777777777778888887I77777777$88888Z777777777Z88888O777777777778888888777777777777778DDDDDDDD87777777777777777777778N
77777777DDDDDDDDDDDD7777777777777777Z88888DDZ77777777777O888888777777777788888Z777777777O888887777777777788888887777777777777$DDDDDDDDD$77777777777777777777DDDD
777777777$DDDDDDDDDDD877777777777777778D88888O77777777777888888777777777788888Z777777777888888777777777788888887777777777777O8DDDDDDD8777777777777777777778DDDDD
77777777777$DDDDDDDDDDD777777777777777$8888888$I777777777788888877777777788888Z77777777788888O77777777778888887777777777777OD888DDD8$77777777777777777778DDDDDDD
7777777777777ZDDDDDDDDDDO7777777777777I7D88888D$7777777777888888777777777O8888Z777777777888887777777777888888O77777777777ID888D8DD877777777777777777778DDDDDDDDD
777777777777777OD~~~~~~~~:$I7777:~~~~~~77888888=~~~~~~7777788888$777777777888=~~~~~~~=7$8O:~~~~~~~~~~~888Z~~~~~~~::~~~~~~:888D~~~~~~~,7~~~~~~~?777778DDDDDDDDDDD
77777777777777777~OOOOOOOZ~:7777:ZOOOO~I77888=+OOOOOOO~:I77Z888887777777778==OOZOOOO?=7888,OOOOOOOOOO~Z88D~OOOOOZ?IOZOOOZ~I8D8~7OOOZOI=ZOOOOO~II77ODDDDDDDDDDDDD
77777777777777777~OOOOOOOOO7?Z77:OOOOO~I777O:?ZOOOOOOOOZ=77788888$777777778~OOOOOOOO?+7888:OOOOOOOOOO:Z8888~OOOOOOOOOOOO:I$888O:8OOOOOOOOOOO~+?I8DDDDDDDDDDDDD$7
77777777777777777~OOOOOZOOOO+8O7:OOOOO~I777I=ZOOOOOOOOOO~II7Z8888877777777O~OOOO~~~~~+7888:OOOOO=~~~~~$88877~$OOOOOOOO+~I78D8$77~ZOOOOOOOOO~?I8DD8DDDDDDDDD8$777
$7777777777777777~OOOOO8OOOZ?O88:OOOOO~I7777ZZOOOO~OOOOO:?777O8888777777778:OOOOOOOO?:Z888:OOOOOOOOOO~$88$777~ZOOOOOO7~I888O7777I~OOOOOOOO:=I8DDDDDDDDDDDZI77777
DD877777777777777~OOOOOOOOO7?888:OOOOO~I7777ZOOOOO~OOOOO:?777I8888Z7777777OZ:OOOOOOOOZ?OO8:OOOOOOOOZO:88$777I~OOOOOOOZ:Z88$777777$~$OOOOO~?I8DD8DDDDDDD777777777
DDDN8O77777777777~OOOOOZOOOO+O88:OOOOO~I7777?OOOOZOOOOOO~?7777Z88887777777Z~~~~~:OOOOO+Z88:OOOOO=~~~~=DO7777:OOOOOOOOO7~877777777I7~OOOOO:?DDDDDDDDDZ77777777777
DDDDDDD8777777777~OOOOOOOOO$?Z88:OOOOO~I7777:ZOOOOOOOOOO~?777778888O77777I7~OOOOOOOZOI?ZOO:OOOOOOOOOO~I7I7I~OOOOOOOOOOOO:7777777777~OOOOO~DDDDDDD8$7777777777777
DDDDDDDDDDO777777~OOOOOOOO+:IIO8:OOOOO~I77777:7OOOOOOOOO:?7777778O8O7777777~OOOOOOOO~=IO8O,OOOOOOOOOO~I77I~ZOOOOZ~7OOOOOZ~777777777~OOOOO:D88DD87I77777777777777
DDDDDDDDD8DD8$777~~~~~~~~~II7777:~~~~~:?7777I7+~~+?+~~?I??777777O88O7777777:~~~~~~~~II8OO$:~~~~~~~~~~~I77+~~~~~~:+:~~~~~~~I7777777Z~~~~~~:D88$777777777777777777
8DDDDDDDDDDD8888I7IIIIIIII7$77777IIIIIIIOO7777I7II?I?I8O8$7777777OOOO777777OIIIIII7I78OOOIIIIIIIIIIIII?7I7?IIIIIII8IIIIIIII77777Z88DIIIIIID777777777777777777777
777ODDDDDDDDDD88D8Z777777777777777778888888777I77I777ZO88OI777777ZOOO$77777$OOO777777OOO$I77777OO8OZ7777777788888O777777777777O888888888Z77777777777777777777777
7777778DDDDDDD888888ZI777777777777777O888888Z777777777O88OO7777777OOOO777777OOO777777OOO777777ZOOOO77777777O8888$777777777777O88888888Z7777777777777777777777777
777777777ODD888888888887777777777777777888888O777777777ZOOOO777777ZOOO777777OOO77777ZOOO777777OOOO7777777$8888OI7777777777$O888888887777777777777777777777778DDD
777777777777O8DD888888888OI77777777777777O88888O7777I777ZOOOO777777OOO$77777OOO77777OOOZ77777OOOO7777I77O8888Z7777777777$88888888O7777777777777777777777$DDDDDDD
777777777777777$888888888888$77777777777777O8888O$~:+ZZ$=~+8OO77777I~~IZ$?~:OOO7777~ZZZZ$$+~:OOO7777~ZZZ$ZZZZ$?+777777$O8888888$77777777777777777777IO88DDDDDDDD
7777777777777777777O8888888888877777777777777O8OO:ZOZZZZZZ?=8OZ77$:?ZZZZZZZZ:~O7777~ZZZZZZZZZ+=$7777~ZZZZZZZZZ?=77777O888888O77777777777777777777$D8D8DDDDDDDDDD
7777777777777777777777OD888888888Z777777777777$8:ZZZZZZZZZZIIOOO7~ZZZZZZZZZZ7~$I777~ZZZZZZZZZZ=:I777~ZZZZZZZZZ?=77$O888888$7777777777777777777O888888DDDDDDDDDDD
7777777777777777777777777Z8888888888$77777777777IZZZZZI$ZZZO?OOO$:ZZZZZ$OZZZZZ=7777~ZZZZZZZZZZZ~777$~ZZZZZ~~~~:+$O8888OO77777777777777777IZ8888888888888DDDD8777
7777777777777777777777777777$888888888O777777777ZZZZZO:++++=?IOOO~ZZZZO~~ZZZZZ:?777~ZZZZZ~ZZZZZ:I7ZO~ZZZZZZZZZ~I8OO88Z7I777777777777777O88888888888888DO$7777777
D8DZ7777777777777777777777777777O8888888O$777777$ZZZZO:ZZZZO?I7ZO~ZZZZZ~+ZZZZZ~?777~ZZZZZ~ZZZZZ~IOOO~ZZZZZZZZZ~I8OO7777777777777777Z888888888888888Z777777777777
DDDDDD88O77777777777777777777777777O888888OO$777~ZZZZZZ$ZZZ$?I77Z:ZZZZZZZZZZZZ+I777~ZZZZZZZZZZ$~IOO$~ZZZZZZZZZO:8I7777777777777788888888888888O$7777777777777777
DDDDDD88888D8O777777777777777777777777$O88OOOOZ7:=OZZZZZZZZ:I77777+ZZZZZZZZZZ~I777Z~ZZZZZZZZZO:?ZO77~ZZZZZZZZZZ:777777777777O8888888888888Z777777777777777777777
DDDDDD888888888888O$7777777777777777777777O8OOOOO~=ZZZZZZ$:=IZ7777~~OZZZZZZ7~II$77O~ZZZZZZZO~~II$777~ZZZZZZZZZZ:77777777$O88888888888Z77777777777777777777777777
DDD8D888888888888888888O$77777777777777777777OOOOOI+~~:::+?IOOZ77777?~:~~:=IIIO777O::::::~=?IIZ77777~::~~::::::~77I77ZOO88888888O$777777777777777777777777777777
77I77Z88D8888888888888888888OZ777777777777777777ZOOO8Z7777$7I$OO$77778Z777Z$I7O$77O$$7OO7$$ZOZI7777OOOO$$7777777$$8OO88O888O$77777777777777777777777777777777777
777777777777$O8888888888888888888OZ7777777777777777$OOOOO777777ZOO7777OZ77ZO77O$77O77ZZ777OO77777OOO$777777777ZOOOOOOO8O77777777777777777777777777777777777777$O
77777777777777777777$O888888888888888OOZ777777777777777OOOOZ77777ZOZ777O$77Z77Z$7$$77O777OZ7777ZOO77777777$OOOOOOOO$777777777777777777777777777777777ZO88DDDDDDD
7777777777777777777777777777$O888888OOOOOOOOO$777777777777ZOOO77777ZZ777Z$77Z7Z$7Z77Z777Z7777ZOZ7777777ZOOOOOO$I7777777777777777777777777777ZO8888888888888DDDDD
777777777777777777777777777777777777ZOOO8OOOOOOOOO$777777777IZOOZ7777OZ77Z77Z7$$7Z7$77$Z777ZO$77777$ZOOOOZ7777777777777777777777777ZO8888888888888888888888DDDDD
7777777777777777777777777777777777777777777$ZOOOOOOOOOOZ77777777$OZ$777Z$7Z77Z7$$$7Z7Z$77ZZ77777ZOOOZ77777777777777777777$ZOO8OO888888888888888888888888888DDDDD
7777777777777777777777777777777777777777777777777$OOOOOOOOO$7777777ZZ$77ZZ7Z7Z7ZZ7Z7Z77ZO7777$OZOZ77777777777777777ZOOOO8OOO888888888888888888888888888888888ZZ$
*/

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class SharePref {

    public static final String CURRENT_USER_ID = "currentUser";
    public static final String KEY_PREFS_FIRST_USE = "firstUse";
    public static final String SELECTED_REGION = "region";

    private SharePref() {
        // Do not allow this to be instantiated.
    }

    public static void setBooleanPref(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Get a boolean preference value based on the key provided.  If the key/value pair is not found return the defaultValue passed in.
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return the value of the preference or, if missing, the defaultValue
     */
    public static boolean getBooleanPref(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (key.equals(KEY_PREFS_FIRST_USE)){
            return true;
        }
        else if (key.equals(SELECTED_REGION)){
            return false;
        }
        else {
            return prefs.getBoolean(key, defaultValue);
        }
    }

    public static void setIntPref(Context context, String key, Integer value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, -1);
    }
}
