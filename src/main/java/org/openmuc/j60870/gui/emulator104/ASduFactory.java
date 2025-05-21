package org.openmuc.j60870.gui.emulator104;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.*;

public class ASduFactory {
    /**
     * Создает объект ASdu определенного типа
     *
     * @param typeASdu      тип создаваемого ASdu
     * @param cause         причина передачи
     * @param commonAddress общий адрес ASdu
     * @param ioa           адрес объекта информации
     * @param value         значение
     * @param quality       качество в формате десятичного числа
     * @return              готовый объект ASdu
     */
    public static ASdu createASdu(int typeASdu, int cause, int commonAddress, int ioa, Object value, int quality) {
        ASduType aSduType = ASduType.typeFor(typeASdu);
        boolean overflow = (quality & 0x01) != 0;
        boolean blocked = (quality & 0x10) != 0;
        boolean substituted = (quality & 0x20) != 0;
        boolean notTopical = (quality & 0x40) != 0;
        boolean invalid = (quality & 0x80) != 0;
        InformationObject informationObject;
        IeTime56 timeTag56 = new IeTime56(System.currentTimeMillis());
        switch (aSduType) {
            case M_SP_NA_1:
                boolean spValue = ((int) value != 0);
                informationObject = new InformationObject(ioa, new IeSinglePointWithQuality[]{
                        new IeSinglePointWithQuality(spValue, blocked, substituted, notTopical, invalid)
                });
                break;
            case M_DP_NA_1:
                IeDoublePointWithQuality.DoublePointInformation dpValue = intToDPI((int) value);
                informationObject = new InformationObject(ioa, new IeDoublePointWithQuality[]{
                        new IeDoublePointWithQuality(dpValue, blocked, substituted, notTopical, invalid)
                });
                break;
            case M_ME_NC_1:
                float measValue = ((Number) value).floatValue();
                informationObject = new InformationObject(ioa, new IeShortFloat(measValue), new IeQuality(overflow, blocked, substituted, notTopical, invalid));
                break;
            case M_SP_TB_1:
                boolean spTimeTagValue = ((int) value != 0);
                informationObject = new InformationObject(ioa, new IeSinglePointWithQuality(spTimeTagValue, blocked, substituted, notTopical, invalid), timeTag56);
                break;
            case M_DP_TB_1:
                IeDoublePointWithQuality.DoublePointInformation dpTimeTagValue = intToDPI((int) value);
                informationObject = new InformationObject(ioa, new IeDoublePointWithQuality(dpTimeTagValue, blocked, substituted, notTopical, invalid), timeTag56);
                break;
            case M_ME_TF_1:
                float measTimeTagValue = ((Number) value).floatValue();
                informationObject = new InformationObject(ioa, new IeShortFloat(measTimeTagValue), new IeQuality(overflow, blocked, substituted, notTopical, invalid), timeTag56);
                break;
            default:
                throw new IllegalArgumentException("Unsupported ASdu type: " + aSduType);
        }

        boolean isSequence = true;
        boolean positiveConfirmation = false;
        boolean test = false;
        int originatorAddress = 0;

        return new ASdu(aSduType, isSequence, CauseOfTransmission.causeFor(cause), test, positiveConfirmation, originatorAddress, commonAddress, informationObject);
    }

    private static IeDoublePointWithQuality.DoublePointInformation intToDPI(int value) {
        switch (value) {
            case 0:
                return IeDoublePointWithQuality.DoublePointInformation.INDETERMINATE_OR_INTERMEDIATE;
            case 1:
                return IeDoublePointWithQuality.DoublePointInformation.OFF;
            case 2:
                return IeDoublePointWithQuality.DoublePointInformation.ON;
            case 3:
                return IeDoublePointWithQuality.DoublePointInformation.INDETERMINATE;
            default:
                throw new IllegalArgumentException("Invalid Double Point value: " + value);
        }
    }
}
