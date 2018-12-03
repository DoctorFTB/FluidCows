package ftblag.fluidcows.util.storage;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public interface IFluidHelper extends IFluidHandler {

    FluidTank getTank();

    int getAmount();

    @Override
    default IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{new FluidTankProperties(getTank().getInfo().fluid, getTank().getInfo().capacity, true, true)};
    }

    @Override
    default int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    default int fillCopy(FluidStack resource, boolean doFill) {
//        getTank().setCanFill(true);
//        int ret = getTank().fillInternal(resource, doFill);
//        getTank().setCanFill(false);
        int ret = getTank().fillInternal(resource, doFill);
        return ret;
    }

    @Nullable
    @Override
    default FluidStack drain(FluidStack resource, boolean doDrain) {
        return getTank().drain(resource, doDrain);
    }

    @Nullable
    @Override
    default FluidStack drain(int maxDrain, boolean doDrain) {
        return getTank().drain(maxDrain, doDrain);
    }
}
