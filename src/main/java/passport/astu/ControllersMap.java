package passport.astu;

import java.util.HashMap;

public class ControllersMap {
    private final HashMap<String, String> controllersMap;
    public ControllersMap() {
        controllersMap = new HashMap<>();
        controllersMap.put("ARIS", "Многофункциональный контроллер");
        controllersMap.put("TOPAZ", "Сервер доступа к данным TOPAZ IEC DAS");
        controllersMap.put("SM160", "Интеллектуальный контроллер");
        controllersMap.put("TM3Com", "Устройство телемеханики");
        controllersMap.put("Контур", "Контроллер телемеханики");
        controllersMap.put("Телеканал", "Комплекс телемеханики");
        controllersMap.put("КИПП", "Счетчик электронный многофункциональный");
        controllersMap.put("Гранит", "Информационно-управляющий теелмеханический комплекс");
        controllersMap.put("Элькон", "Устройство сбора и передачи данных");
        controllersMap.put("ЭНКС", "Устройство сбора данных");
    }

    public String getControllerFullName(String controllerName) {
        controllerName = controllerName.toLowerCase();
        if (controllerName.contains("aris")) {
            return controllersMap.get("ARIS");
        } else if (controllerName.contains("mx")) {
            return controllersMap.get("TOPAZ");
        } else if (controllerName.contains("sm") && controllerName.contains("160")) {
            return controllersMap.get("SM160");
        } else if (controllerName.contains("tm") && controllerName.contains("com")) {
            return controllersMap.get("TM3Com");
        } else if (controllerName.contains("контур")) {
            return controllersMap.get("Контур");
        } else if (controllerName.contains("телеканал")) {
            return controllersMap.get("Телеканал");
        } else if (controllerName.contains("кипп")) {
            return controllersMap.get("КИПП");
        } else if (controllerName.contains("гранит")) {
            return controllersMap.get("Гранит");
        } else if (controllerName.contains("элькон")) {
            return controllersMap.get("Элькон");
        } else if (controllerName.contains("энкс")) {
            return controllersMap.get("ЭНКС");
        } else return "";
    }
}
