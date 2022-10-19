package com.playtika.finalproject.models;
import com.playtika.finalproject.models.exceptions.NotAValidPlatformNumber;
import lombok.Getter;

@Getter
public enum PlatformType {
    PC(6), PS(48), XBOX(49);

    private int platformNumber;

    PlatformType(int platformNumber) {
        this.platformNumber = platformNumber;
    }

    public static PlatformType valueOfPlatform(int platformNumber) {
        for (PlatformType platformType : values()) {
            if (platformType.platformNumber == platformNumber) {
                return platformType;
            }
        } throw new NotAValidPlatformNumber("It's not a valid platform number");

    }
}
