package passport.astu;

import java.util.HashMap;

public class InstructionMap {
    private final HashMap <String, String> instructionMap;
    private final HashMap <String, String> techCardMap;


    public InstructionMap() {
        instructionMap = new HashMap<>();
        instructionMap.put("ARIS-11", "ПБКМ.424359.012 РЭ");
        instructionMap.put("ARIS-28", "ПБКМ.424359.016 РЭ");
        instructionMap.put("MX240", "ПЛСТ.421457.100 РЭ");
        instructionMap.put("MX681", "ПЛСТ.421457.105 РЭ");
        instructionMap.put("ENIP-2", "ЭНИП.411187.002 РЭ");
        instructionMap.put("ENKS-3", "ЭНКС.403500.001 РЭ");
        instructionMap.put("ENMV-1", "ЭНМВ.423000.001 РЭ");

        techCardMap = new HashMap<>();
        techCardMap.put("ARIS", "473 от 8.05.2024");
        techCardMap.put("TOPAZ", "473 от 8.05.2024");
        techCardMap.put("Algorithm", "");
        techCardMap.put("Kontur", "472 от 26.08.2022");
        techCardMap.put("Granit", "471 от 26.08.2022");
    }

    public String getInstruction(String device) {
        device = device.toLowerCase();
        if (device.contains("aris") && device.contains("28")) {
            return instructionMap.get("ARIS-28");
        } else if (device.contains("aris") && device.contains("11")) {
            return instructionMap.get("ARIS-11");
        } else if (device.contains("энкс")) {
            return instructionMap.get("ENKS-3");
        } else if (device.contains("mx") && device.contains("240")) {
            return instructionMap.get("MX240");
        } else if (device.contains("mx") && device.contains("681")) {
            return instructionMap.get("MX681");
        } else return "";
    }

    public String getTechCard(String device) {
        device = device.toLowerCase();
        if (device.contains("aris")) {
            return techCardMap.get("ARIS");
        } else if (device.contains("mx") && (device.contains("240") || device.contains("681"))) {
            return techCardMap.get("TOPAZ");
        } else if (device.contains("телеканал") || device.contains("кипп") || (device.contains("tm") && device.contains("com"))) {
            return techCardMap.get("Algorithm");
        } else if (device.contains("контур")) {
            return techCardMap.get("Kontur");
        } else if (device.contains("гранит")) {
            return techCardMap.get("Granit");
        } else return "";
    }
}
