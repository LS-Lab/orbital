package orbital.moon.logic;

import orbital.logic.sign.*;
import orbital.logic.sign.type.*;
import orbital.logic.imp.*;

class ClassicalLogicPiTrial extends ClassicalLogic {
    public static void main(String arg[]) {
	new ClassicalLogicPiTrial().parseTypeExpressionTest();
    }
    private final TypeSystem typeSystem = Types.getDefault();
    
    private final void parseTypeExpressionTest() {
	try {
	    Type t;
	    Symbol tau = new SymbolBase("tau", typeSystem.TYPE(), null, true);
	    Symbol MAP = coreSignature().get("->", typeSystem.map(typeSystem.product(new Type[] {typeSystem.TYPE(), typeSystem.TYPE()}), typeSystem.TYPE()));
	    Symbol truth = coreSignature().get("truth", typeSystem.TYPE());
	      /*
	      t = new PiAbstractionType(tau,
	      (Formula) compose(createAtomic(MAP), new Expression[] {createAtomic(tau), createAtomic(truth)}),
	      InterpretationBase.EMPTY(coreSignature()));
	      System.err.println(t);
	      Symbol PRODUCT = coreSignature().get("*", typeSystem.map(typeSystem.list(typeSystem.TYPE()), typeSystem.TYPE()));
	      Symbol sigma = new SymbolBase("sigma", typeSystem.TYPE(), null, true);
	      t = new PiAbstractionType(sigma,
	      new PiAbstractionExpression(tau, (Formula)
	      compose(createAtomic(MAP), new Expression[] {
	      compose(createAtomic(PRODUCT), new Expression[] {new FormulaSequence(new Expression[] {createAtomic(sigma), createAtomic(tau)})}),
	      compose(createAtomic(MAP), new Expression[] {createAtomic(sigma), createAtomic(tau)})
	      })),
	      InterpretationBase.EMPTY(coreSignature()));
	      System.err.println(t);
	      t = parseTypeExpression("(\\\\s . (s->truth))");
	      System.err.println(t);
	      t = parseTypeExpression("(\\\\s . (s->truth)->truth)");
	      System.err.println(t);
	      Symbol ALL2 = new SymbolBase("o", t);
	      System.err.println(createAtomic(ALL2));
	      System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
	      createAtomic(new SymbolBase("p", typeSystem.map(typeSystem.objectType(String.class, "string"), Types.TRUTH))),
	      })));
	      System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
	      createAtomic(new SymbolBase("p2", typeSystem.map(typeSystem.objectType(orbital.math.Integer.class, "integer"), Types.TRUTH))),
	      })));
	      System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
	      createAtomic(new SymbolBase("p3", typeSystem.map(typeSystem.objectType(orbital.math.Real.class, "real"), Types.TRUTH))),
	      })));
	      System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
	      createAtomic(new SymbolBase("p4", typeSystem.map(Types.TRUTH, Types.TRUTH))),
	      })));
	      System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
	      createAtomic(new SymbolBase("p5", typeSystem.map(typeSystem.map(typeSystem.objectType(String.class, "string"), Types.TRUTH), Types.TRUTH))),
	      })));
	      System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
	      createAtomic(new SymbolBase("p6", typeSystem.map(typeSystem.map(typeSystem.map(typeSystem.objectType(String.class, "string"), Types.TRUTH), Types.TRUTH), Types.TRUTH))),
	      })));*/

	    System.err.println();
	    setEnableTypeChecks(false);

	    t = parseTypeExpression("(\\\\s . (s->truth)->truth)");
	    System.err.println(t);

	    {
		// construction of type parseTypeExpression("(\\\\s . (s->truth)->truth)") without parsing
		final Symbol s = new SymbolBase("s", Types.getDefault().TYPE(), null, true);
		final Expression se = createAtomic(s);
		final Expression helperType = compose(createAtomic(MAP), new Expression[] {
		    compose(createAtomic(MAP), new Expression[] {se, createAtomic(truth)}),
		    createAtomic(truth)
		});
		final Expression tve = compose(createAtomic(PI), new Expression[] {se, helperType});
		System.err.println(tve + " = " + t);
		final Type tv = LogicParser.myasType(tve, coreSignature());
		System.err.println(tv + " = " + t);
		System.err.println(tv.equals(t));
	    }
	    
	    Symbol ALL2 = new SymbolBase("o", t);
	    System.err.println("New universal quantifier " + createAtomic(ALL2) + " applying");
	    System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
		createAtomic(new SymbolBase("p", typeSystem.map(typeSystem.objectType(String.class, "string"), Types.TRUTH))),
	    })));
	    System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
		createAtomic(new SymbolBase("p2", typeSystem.map(typeSystem.objectType(orbital.math.Integer.class, "integer"), Types.TRUTH))),
	    })));
	    System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
		createAtomic(new SymbolBase("p3", typeSystem.map(typeSystem.objectType(orbital.math.Real.class, "real"), Types.TRUTH))),
	    })));
	    System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
		createAtomic(new SymbolBase("p4", typeSystem.map(Types.TRUTH, Types.TRUTH))),
	    })));
	    System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
		createAtomic(new SymbolBase("p5", typeSystem.map(typeSystem.map(typeSystem.objectType(String.class, "string"), Types.TRUTH), Types.TRUTH))),
	    })));
	    System.err.println(Types.toTypedString(compose(createAtomic(ALL2), new Expression[] {
		createAtomic(new SymbolBase("p6", typeSystem.map(typeSystem.map(typeSystem.map(typeSystem.objectType(String.class, "string"), Types.TRUTH), Types.TRUTH), Types.TRUTH))),
	    })));
	    System.err.println("are the results\n");
	    

	    t = parseTypeExpression("(\\\\_s . (\\\\_t . (_s \u00d7 _t)->(_s->_t)))");
	    System.err.println(t);
	    System.err.println("apply binary " + Types.toTypedString(compose(createAtomic(new SymbolBase("w", t)), new Expression[] {
		new ProductFormula(new Expression[] {
		    createAtomic(new SymbolBase("x", typeSystem.objectType(String.class, "string"))),
		    createAtomic(new SymbolBase("y", typeSystem.objectType(orbital.math.Real.class, "real")))
		})
	    })));
	    System.err.println("apply binary " + Types.toTypedString(compose(createAtomic(new SymbolBase("w", t)), new Expression[] {
		new ProductFormula(new Expression[] {
		    createAtomic(new SymbolBase("x", typeSystem.objectType(String.class, "string"))),
		    createAtomic(new SymbolBase("y", typeSystem.objectType(orbital.math.Real.class, "real")))
		})
	    })));


	    /*t = parseTypeExpression("(\\\\s . s->(s->s))");
	      System.err.println(t);
	      t = parseTypeExpression("(\\\\s . (\\\\t . s))");
	      System.err.println(t);*/
	    t = parseTypeExpression("(\\\\_s . (\\\\_t . _s->(_t->(_s->_t))))");
	    System.err.println(t);
	    //t = parseTypeExpression("(\\\\_s . _s -> (\\\\_t . _t->(_s->_t)))");
	    //System.err.println(t);
	    /*
	      Type pt;
	      System.err.println();
	      System.err.println(" >>> param type1= " + (pt = calculateParameterTypeForPiAbstraction((PiAbstractionType)t, typeSystem.objectType(String.class, "string"))));
	      System.err.println();
	      Type t2;
	      System.err.println(" >>> instantiat1= " + (t2 = ((PiAbstractionType)t).apply(pt)));
	      System.err.println();
	      //System.err.println(" >>> param type2= " + (pt = calculateParameterTypeForPiAbstraction((PiAbstractionType)t2, typeSystem.objectType(orbital.math.Real.class, "real"))));
	      pt = typeSystem.objectType(orbital.math.Real.class, "real");
	      System.err.println();
	      System.err.println(" >>> instantiat2= " + ((PiAbstractionType)t2).apply(pt));
	      System.err.println(" >>> param type2= " + (pt = calculateParameterTypeForPiAbstraction((PiAbstractionType)t2, typeSystem.objectType(orbital.math.Real.class, "real"))));
	      System.err.println();
	      System.err.println(" >>> instantiat2= " + ((PiAbstractionType)t2).apply(pt));
	      System.err.println();
	    */
	    //@fixme w(x) has the wrong type, with t already bound to string without any reasons
	    System.err.println("apply once " + Types.toTypedString(compose(createAtomic(new SymbolBase("w", t)), new Expression[] {
		createAtomic(new SymbolBase("x", typeSystem.objectType(orbital.math.Real.class, "real"))),
	    })));
	    System.err.println();
	    System.err.println("apply once " + Types.toTypedString(compose(createAtomic(new SymbolBase("w", t)), new Expression[] {
		createAtomic(new SymbolBase("x", typeSystem.objectType(String.class, "string"))),
	    })));
	    System.err.println();
	    System.err.println("apply twice " + Types.toTypedString(compose(compose(createAtomic(new SymbolBase("w", t)), new Expression[] {
		createAtomic(new SymbolBase("x", typeSystem.objectType(String.class, "string"))),
	    }), new Expression[] {
		createAtomic(new SymbolBase("y", typeSystem.objectType(orbital.math.Real.class, "real"))),
	    })));
	    /*t = parseTypeExpression("(\\\\s . (\\\\t. t))");
	      System.err.println(t);
	      t = parseTypeExpression("(\\\\s . (\\\\t . s->(t->(s->t))))");
	      System.err.println(t);
	      Symbol LAMBDA2 = new SymbolBase("l", t);
	      System.err.println(createAtomic(LAMBDA2));
	      System.err.println("partial " + Types.toTypedString(compose(createAtomic(LAMBDA2), new Expression[] {
	      createAtomic(new SymbolBase("x", typeSystem.objectType(String.class, "string"), null, true)),
	      })));
	      System.err.println("complet " + Types.toTypedString(compose(compose(createAtomic(LAMBDA2), new Expression[] {
	      createAtomic(new SymbolBase("x", typeSystem.objectType(String.class, "string"), null, true)),
	      }), new Expression[] {
	      createAtomic(new SymbolBase("a", typeSystem.objectType(Boolean.class, "truth"))),
	      })));*/
	} catch (orbital.logic.sign.ParseException ex) {
	    throw (InternalError) new InternalError("Unexpected syntax in internal term construction")
		.initCause(ex);
	}
    }
}
