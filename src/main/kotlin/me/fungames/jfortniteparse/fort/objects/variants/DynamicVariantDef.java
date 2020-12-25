package me.fungames.jfortniteparse.fort.objects.variants;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.exports.variants.VariantTypeBase;
import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

@UStruct
public class DynamicVariantDef extends BaseVariantDef {
    public List<Lazy<VariantTypeBase>> DynamicVariants;
}
