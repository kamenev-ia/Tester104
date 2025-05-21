package passport.astu;

import java.util.HashMap;

public class ModulesMap {
    private final HashMap<String, String> modulesMap;

    public ModulesMap() {
        modulesMap = new HashMap<>();
        modulesMap.put("ЭНМВ", "Модуль ввода/вывода");
        modulesMap.put("ЭНИП", "Преобразователь измерительный многофункциональный");
        modulesMap.put("HVD3", "Устройство телемеханики TOPAZ");
        modulesMap.put("PM7", "Модуль измерительный многофункциональный TOPAZ");
        modulesMap.put("DIN", "Модуль телесигнализации TOPAZ");
        modulesMap.put("MTU", "Модуль телемеханики TOPAZ");
        modulesMap.put("PSC", "Модуль контроля питания TOPAZ");
        modulesMap.put("КИПП", "Счетчик электронный многофункциональный");
        modulesMap.put("ЭНМИ", "Модуль индикации");
        modulesMap.put("ARIS-2808E", "Крейт расширения");
        modulesMap.put("TS32", "Модуль дискретного ввода");
        modulesMap.put("TC4", "Модуль телеуправления");
        modulesMap.put("ST410", "Контроллер ввода-вывода");
        modulesMap.put("Телеканал", "Устройство телемеханики");

    }

    public String getModulesFullName(String moduleName) {
        moduleName = moduleName.toLowerCase();
        if (moduleName.contains("энмв")) {
            return modulesMap.get("ЭНМВ-1");
        } else if (moduleName.contains("энип")) {
            return modulesMap.get("ЭНИП");
        } else if (moduleName.contains("rtu")) {
            return modulesMap.get("HVD3");
        } else if (moduleName.contains("pm") && moduleName.contains("7")) {
            return modulesMap.get("PM7");
        } else if (moduleName.contains("din") && (moduleName.contains("32") || moduleName.contains("16"))) {
            return modulesMap.get("DIN");
        } else if (moduleName.contains("mtu") && (moduleName.contains("3") || moduleName.contains("5"))) {
            return modulesMap.get("MTU");
        } else if (moduleName.contains("psc")) {
            return modulesMap.get("PSC");
        } else if (moduleName.contains("кипп")) {
            return modulesMap.get("КИПП");
        } else if (moduleName.contains("энми")) {
            return modulesMap.get("ЭНМИ");
        } else if (moduleName.contains("2808e")) {
            return modulesMap.get("ARIS-2808E");
        } else if (moduleName.contains("ts") && moduleName.contains("32")) {
            return modulesMap.get("TS32");
        } else if (moduleName.contains("tc") && moduleName.contains("4")) {
            return modulesMap.get("TC4");
        } else if (moduleName.contains("st") && moduleName.contains("410")) {
            return modulesMap.get("ST410");
        } else if (moduleName.contains("телеканал")) {
            return modulesMap.get("Телеканал");
        } else return "";
    }
}
