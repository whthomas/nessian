package top.nessian.server.model;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;

/**
 * Created by whthomas on 15/11/11.
 */
public class HessianStream {

    private AbstractHessianInput hessianInput;
    private AbstractHessianOutput hessianOutput;

    public HessianStream(AbstractHessianInput hessianInput,
                         AbstractHessianOutput hessianOutput) {
        this.hessianInput = hessianInput;
        this.hessianOutput = hessianOutput;
    }

    public AbstractHessianInput getHessianInput() {
        return hessianInput;
    }

    public void setHessianInput(AbstractHessianInput hessianInput) {
        this.hessianInput = hessianInput;
    }

    public AbstractHessianOutput getHessianOutput() {
        return hessianOutput;
    }

    public void setHessianOutput(AbstractHessianOutput hessianOutput) {
        this.hessianOutput = hessianOutput;
    }
}
