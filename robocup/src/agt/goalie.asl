// Agent goalie in project robocup

/* Initial beliefs and rules */

out_of_net.

// IMPORTANT NOTE S == 1 denotes friendly goal and S == 0 denotes opposing goal

close(goal) :- goal(S, D, A) & D <= 1 & S == 1.

facing(goal) :- goal(S, D, A) & (A <= 5) & (A >= -5) & S == 1.

/* Plans */

+goal(S, D, A) : A > 5 & S == 1 & out_of_net
    <- turn(A).

+goal(S, D, A) : A < -5 & S == 1 & out_of_net
    <- turn(A).

+goal(S, D, A) : facing(goal) & out_of_net
    <- !move_forward.

+goal_1(lost) : out_of_net <- .print("lost goal"); turn(20).

//dash towards goal if facing it.
+!move_forward : not(close(goal)) & facing(goal) <- dash(25); !move_forward.

//if goal is moved while executing plan, slow down
+!move_forward : not(close(goal)) & not(facing(goal)) <- dash(5).

+!move_forward : close(goal) <- .print("reached goal"); -out_of_net.









