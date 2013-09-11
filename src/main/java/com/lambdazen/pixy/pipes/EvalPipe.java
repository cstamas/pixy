package com.lambdazen.pixy.pipes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lambdazen.pixy.PipeVisitor;
import com.lambdazen.pixy.PixyDatum;
import com.lambdazen.pixy.PixyPipe;
import com.lambdazen.pixy.gremlin.GremlinPipelineExt;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class EvalPipe implements PixyPipe, InternalLookupPipe {
	private String outNamedStep;
	private List<PixyDatum> ops;

	public EvalPipe(String varName, List<PixyDatum> ops) {
		this.outNamedStep = varName;
		this.ops = ops;
	}

	public String toString() {
		return "pixyEval(" + ops + ")"
		+ ((outNamedStep == null) ? "" : " -> as('" + outNamedStep + "')");
	}

	@Override
	public GremlinPipeline pixyStep(GremlinPipeline inputPipe) {	
		if (outNamedStep == null) {
			return GremlinPipelineExt.pixyEval(inputPipe, ops);
		} else {
			return GremlinPipelineExt.pixyEval(inputPipe, ops).as(outNamedStep);
		}
	}

	@Override
	public void visit(PipeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Set<String> getDependentNamedSteps() {
		Set<String> ans = new HashSet<String>();
		for (PixyDatum op : ops) {
			if (op.isAtomAPipeVar()) {
				ans.add(op.getAtomVarName());
			}
		}
		
		return ans;
	}
}
