package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortPersonalVehicleItemDefinition extends FortAccountItemDefinition {
    public FSoftObjectPath /*SoftClassPath*/ PersonalVehicleAbility;
    public Float MountTime;
    public FSoftObjectPath SkeletalMesh;
    public FPackageIndex /*AnimInstance*/ AnimClass;
    public FSoftObjectPath ActivateSound;
    public FSoftObjectPath DeactivateSound;
}
