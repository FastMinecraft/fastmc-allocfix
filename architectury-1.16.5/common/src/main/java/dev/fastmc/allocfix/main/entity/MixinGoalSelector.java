package dev.fastmc.allocfix.main.entity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(GoalSelector.class)
public class MixinGoalSelector {
    @Shadow
    @Final
    private Supplier<Profiler> profiler;

    @Shadow
    @Final
    private Set<PrioritizedGoal> goals;

    @Shadow
    @Final
    private Map<Goal.Control, PrioritizedGoal> goalsByControl;

    @Shadow
    @Final
    private static PrioritizedGoal REPLACEABLE_GOAL;

    @Shadow
    @Final
    private EnumSet<Goal.Control> disabledControls;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void tick() {
        Profiler profiler = this.profiler.get();
        profiler.push("goalCleanup");
        for (PrioritizedGoal prioritizedGoal : this.goals) {
            if (!prioritizedGoal.isRunning()) continue;
            if (!containsAny(prioritizedGoal.getControls(), disabledControls) && prioritizedGoal.shouldContinue())
                continue;
            prioritizedGoal.stop();
        }
        goalsByControl.values().removeIf(goal -> !goal.isRunning());
        profiler.pop();

        profiler.push("goalUpdate");
        loop: for (PrioritizedGoal goal : this.goals) {
            if (goal.isRunning()) continue;
            if (containsAny(goal.getControls(), disabledControls)) continue;
            for (Goal.Control control : goal.getControls()) {
                if (getGoalForControlOrReplaceable(control).canBeReplacedBy(goal)) continue;
                continue loop;
            }

            if (goal.canStart()) {
                for (Goal.Control control : goal.getControls()) {
                    PrioritizedGoal otherGoal = getGoalForControlOrReplaceable(control);
                    otherGoal.stop();
                    this.goalsByControl.put(control, goal);
                }
                goal.start();
            }
        }
        profiler.pop();

        profiler.push("goalTick");
        for (PrioritizedGoal goal : this.goals) {
            if (!goal.isRunning()) continue;
            goal.tick();
        }
        profiler.pop();
    }

    private PrioritizedGoal getGoalForControlOrReplaceable(Goal.Control control) {
        return this.goalsByControl.getOrDefault(control, REPLACEABLE_GOAL);
    }

    private boolean containsAny(EnumSet<Goal.Control> set, EnumSet<Goal.Control> other) {
        for (Goal.Control control : set) {
            if (other.contains(control)) {
                return true;
            }
        }
        return false;
    }
}
