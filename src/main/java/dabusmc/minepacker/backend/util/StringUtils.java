package dabusmc.minepacker.backend.util;

import dabusmc.minepacker.backend.logging.Logger;

public class StringUtils {

    public static String setVariableInString(String str, String variableName, String value) {
        String toReplace = "${";
        toReplace += variableName;
        toReplace += "}";

        if(str.contains(toReplace)) {
            return str.replace(toReplace, value);
        }

        return str;
    }

    public static String preparePath(String path) {
        return path.replace("\\", "/").replace("//", "/");
    }

    public static String generateURLEncodedString(String... strings) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        for(String str : strings) {
            out.append(str);
            if((i + 1) % 2 == 0) {
                out.append("&");
            } else {
                out.append("=");
            }
            i += 1;
        }
        out.deleteCharAt(out.toString().length() - 1);
        return out.toString();
    }

}
