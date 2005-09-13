/**
 * @(#)WizardView.java 0.9 2000/06/25 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import orbital.logic.functor.Predicate;
import java.awt.Dialog;
import java.awt.Component;
import java.awt.Frame;

import java.awt.Panel;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * WizardView class for GUI wizards. Provides generic functionality for GUI wizards
 * that are easy to use.
 * 
 * <p>Will display an array of components step by step, applying the corresponding
 * predicate on each advancement (or click on "back" if it is not null).
 * First it displays steps[0]. After the first click to "next" it applies actionSteps[0]
 * and advances to display steps[1]. After another click to "next" it will apply actionSteps[1]
 * and display steps[2] and so on.
 * On a click to "back" <em>after decreasing</em> currentStep, actionSteps[currentStep] will be called with the argument "back-"+<code>currentStep</code>
 * before displaying the previous step.
 * A click on "finish" is like continuously hitting "next" until the end of steps is reached.</p>
 * <p>
 * A predicate in the sequence of action steps that is not <span class="keyword">null</span> can return <span class="keyword">false</span>
 * to veto proceeding or resuming to the previous step.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class WizardView extends Dialog {
    /**
     * @serial
     */
    protected int                 currentStep;
    /**
     * @serial
     */
    protected Component[] steps;
    /**
     * @serial
     */
    protected Predicate[] actionSteps;

    /**
     * @serial
     */
    private Component     centerSlide;
    /**
     * @serial
     */
    private Panel                 control;
    /**
     * @serial
     */
    private Button                cancel;
    /**
     * @serial
     */
    private Button                back;
    /**
     * @serial
     */
    private Button                next;
    /**
     * @serial
     */
    private Button                finish;

    /**
     * Create a new WizardView Dialog.
     * @param parent the parent of this dialog.
     * @param title the title of this dialog.
     * @param steps an array of components to display at the corresponding steps.
     * @param actionSteps the action predicates to apply on a click to "next" or "back".
     * Must have the same length as steps.
     */
    public WizardView(Frame parent, String title, Component[] steps, Predicate[] actionSteps) {
        super(parent, title);
        this.currentStep = 0;
        setSteps(steps);
        setActionSteps(actionSteps);
        centerSlide = steps[currentStep];
        add(centerSlide, BorderLayout.CENTER);
        control = new Panel();
        control.setLayout(new FlowLayout());
        control.add(cancel = new Button("Cancel"));
        cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // cancel will notify outer listeners that want to do System.exit or anything
                    WizardView.this.dispatchEvent(new WindowEvent(WizardView.this, WindowEvent.WINDOW_CLOSING));
                } 
            });
        control.add(back = new Button("<< Back"));
        back.setEnabled(false);
        back.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    back();
                } 
            });
        control.add(next = new Button("Next >>"));
        next.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    next();
                } 
            });
        control.add(finish = new Button("Finish"));
        finish.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    finish();
                } 
            });
        add(control, BorderLayout.SOUTH);
        pack();
    }

    // get/set methods
    public Component[] getSteps() {
        return steps;
    } 

    /**
     * Set the components to show at the single steps.
     */
    public void setSteps(Component[] steps) {
        if (actionSteps != null && actionSteps.length != steps.length)
            throw new IllegalArgumentException("incompatible length of steps");
        this.steps = steps;
    } 
    public Predicate[] getActionSteps() {
        return actionSteps;
    } 

    /**
     * Set the actions to take after the single steps.
     */
    public void setActionSteps(Predicate[] actionSteps) {
        if (actionSteps != null && actionSteps.length != steps.length)
            throw new IllegalArgumentException("incompatible length of steps");
        this.actionSteps = actionSteps;
    } 

    // manipulation methods

    /**
     * Move to previous step.
     */
    public void back() {
        if (currentStep - 1 < 0)
            throw new IllegalStateException("back step out of bounds");
        currentStep--;
        if (actionSteps != null && actionSteps[currentStep] != null) {
            if (!actionSteps[currentStep].apply("back-" + (currentStep)))
                return;
        } 
        remove(centerSlide);
        centerSlide = steps[currentStep];
        add(centerSlide, BorderLayout.CENTER);
        validate();
        if (currentStep - 1 < 0)
            back.setEnabled(false);
        next.setEnabled(true);
    } 

    /**
     * Move to next step.
     */
    public void next() {
        if (currentStep + 1 >= steps.length)
            throw new IllegalStateException("next step out of bounds");
        if (actionSteps != null && actionSteps[currentStep] != null) {
            if (!actionSteps[currentStep].apply("next-" + currentStep))
                return;
        } 
        remove(centerSlide);
        centerSlide = steps[++currentStep];
        add(centerSlide, BorderLayout.CENTER);
        validate();
        if (currentStep + 1 == steps.length)
            next.setEnabled(false);
        back.setEnabled(true);
    } 

    /**
     * Move on upto finishing step.
     * Will advance as long as possible using {@link #next()}.
     */
    public void finish() {
        int was = currentStep;

        // do all steps
        int o = currentStep;                    // where we started advancing
        while (currentStep + 1 < steps.length) {
            next();
            if (o >= currentStep)               // stop finishing if we cannot advance any more
                return;
            o = currentStep;
        } 

        if (actionSteps != null && actionSteps[currentStep] != null)
            actionSteps[currentStep].apply("finish-" + was);
        setVisible(false);
    } 
}
