package plugger.model;

public class FormatTime {
	/**
     * Formats milliseconds in m:ss format
     * @param milliseconds
     * @return
     */
    public static String formatMilliseconds(Double milliseconds) {
        milliseconds /= 1000;
        return String.format("%01.0f:%02.0f", milliseconds / 60, milliseconds % 60);
    }

    public static String formatSeconds(int seconds){
    	return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

}
