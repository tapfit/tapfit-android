package co.tapfit.android.helper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import co.tapfit.android.model.Instructor;

/**
 * Created by zackmartinsek on 9/12/13.
 */
public class WorkoutFormat {

    public static String getStartEndDateTime(DateTime startTime, DateTime endTime) {
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .appendClockhourOfHalfday(1)
                .appendLiteral(":")
                .appendMinuteOfHour(2)
                .appendHalfdayOfDayText()
                .toFormatter();


        String start = dateFormatter.print(startTime);
        String end = dateFormatter.print(endTime);

        start = start.substring(0, start.length() - 1).toLowerCase();
        end = end.substring(0, end.length() - 1).toLowerCase();

        String timeString = start + " - " + end;
        return timeString;
    }

    public static String getInstructor(Instructor instructor) {
        String instructor_name = "";
        if (instructor != null) {
            if (instructor.first_name != null) {
                instructor_name = instructor_name + instructor.first_name;
            }
            if (instructor.last_name != null) {
                instructor_name = instructor_name + " " + instructor.last_name;
            }
        }
        return instructor_name;
    }
}
