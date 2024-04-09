// Agent striker in project robocup

/* Initial beliefs and rules */

// IMPORTANT NOTE S == 1 denotes friendly goal and S == 0 denotes opposing goal

close(ball) :- ball(D, A) & D <= 1.

facing(ball) :- ball(D, A) & (A <= 5) & (A >= -5).

// facing opposition goal
facing(goal) :- goal(S, D, A) & (A <= 10) & (A >= -10) & S == 0.

/* Plans */

+ball(D, A) : A > 5 & not(close(ball))
    <- turn(A).

+ball(D, A) : A < -5 & not(close(ball))
    <- turn(A).

+ball(D, A) : facing(ball) & not(close(ball))
    <- !move_forward.

+ball(D, A) : close(ball)
    <- !look_for_goal.

+ball(lost) : true <- 
    .print("lost ball"); !look_for_ball.

+!look_for_ball : not(facing(ball)) <- turn(15); !look_for_ball.

+!look_for_ball : facing(ball) <- turn(5).

//dash towards ball if facing it.
+!move_forward : facing(ball) <- dash(25); !move_forward.

//if ball is moved while executing plan, turn
+!move_forward : not(facing(ball)) <- turn(5); .fail_goal(move_forward).

+!move_forward : close(ball) <- .print("reached ball"); !look_for_goal.

+!look_for_goal : not(facing(goal)) <- .print("looking for goal to shoot at"); turn(35).

+!look_for_goal : facing(goal) <- kick(500, 0).