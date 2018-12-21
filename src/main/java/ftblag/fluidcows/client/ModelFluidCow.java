package ftblag.fluidcows.client;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TexturedQuad;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFluidCow extends ModelQuadruped
{
    public ModelFluidCow()
    {
        super(12, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
        this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
        this.head.setTextureOffset(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
        this.head.setTextureOffset(22, 0).addBox(4.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
        this.body = new ModelRenderer(this, 18, 4);
        this.body.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F);
        this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
        this.body.setTextureOffset(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4, 6, 1);
        --this.leg1.rotationPointX;
        ++this.leg2.rotationPointX;
        this.leg1.rotationPointZ += 0.0F;
        this.leg2.rotationPointZ += 0.0F;
        --this.leg3.rotationPointX;
        ++this.leg4.rotationPointX;
        --this.leg3.rotationPointZ;
        --this.leg4.rotationPointZ;
        this.childZOffset += 2.0F;

        fixUV(head);
        fixUV(body);
        fixUV(leg1);
        fixUV(leg2);
        fixUV(leg3);
        fixUV(leg4);
    }

    private static void fixUV(ModelRenderer model) {

        for (ModelBox box : model.cubeList) {
            for (TexturedQuad it : box.quadList) {

                it.vertexPositions[0].texturePositionX = 0F;
                it.vertexPositions[0].texturePositionY = 1F;

                it.vertexPositions[1].texturePositionX = 1F;
                it.vertexPositions[1].texturePositionY = 1F;

                it.vertexPositions[2].texturePositionX = 1F;
                it.vertexPositions[2].texturePositionY = 0F;

                it.vertexPositions[3].texturePositionX = 0F;
                it.vertexPositions[3].texturePositionY = 0F;
            }
        }
    }
}
