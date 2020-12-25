package me.fungames.jfortniteparse.valorant.exports

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

class CharacterAbilityUIData : UObject() {
    var DisplayName: FText? = null
    var Description: FText? = null
    var DisplayIcon: FPackageIndex? = null
}