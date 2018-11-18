package io.github.eufranio.campfirecooking.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by Frani on 17/11/2018.
 */
@ConfigSerializable
public class MainConfig {

    @Setting
    public List<String> stickLore = Lists.newArrayList(
            "Item: %item%",
            "Time until cook: %time%s",
            "Stay near a campfire to cook!"
    );

    @Setting
    public int cookTime = 10;

}
