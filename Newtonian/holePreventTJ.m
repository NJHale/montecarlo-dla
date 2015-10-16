function co = holePreventTJ (row, col, data)
    neigh = data(row,col+1) + data(row, col-1) + data(row-1,col) + data(row+1,col);%makes a count of how many stuck walkers are currently by the moving walker
    EWcount = data(row+1,col) + data(row-1,col);   %finds if there are stuck walkers to the right or left
    NScount = data(row,col+1) + data(row,col-1);   %finds if there are stuck walkers to the north or south
    if neigh == 1  %if the walker has ONE neighbor
        if (data(row,col+1) == 1 && data(row+1,col-1) == 1)
            co = 1;%stop the walker from forming hole
        elseif (data(row,col+1) == 1 && data(row-1,col-1) == 1)
            co = 1;
        elseif (data(row,col-1) == 1 && data(row+1,col+1) == 1)
            co = 1;
        elseif (data(row,col-1) == 1 && data(row-1,col+1) == 1)
            co = 1;
        elseif (data(row+1,col) == 1 && data(row-1,col-1) == 1)
            co = 1;
        elseif (data(row+1,col) == 1 && data(row-1,col+1) == 1)
            co = 1;
        elseif (data(row-1,col) == 1 && data(row+1,col-1) == 1)
            co = 1;
        elseif (data(row-1,col) == 1 && data(row+1,col+1) == 1)
            co = 1;
        else
            co = 0;%the walker can go here
        end

    elseif neigh == 2
        if EWcount == 2 %walker can not wedge itself between two walkers unless there is a third one either North or South
            co = 1;
        elseif NScount == 2  %walker cannot wedge itself between two walkers unless there is a third one either West or East
            co = 1;
        elseif (data(row,col+1) == 1 && data(row+1,col) == 1 && data(row-1,col-1) == 1)
            co = 1;
        elseif (data(row,col+1) == 1 && data(row-1,col) == 1 && data(row+1,col-1) == 1)
            co = 1;
        elseif (data(row,col-1) == 1 && data(row+1,col) == 1 && data(row-1,col+1) == 1)
            co = 1;
        elseif (data(row,col-1) == 1 && data(row-1,col) == 1 && data(row+1,col+1) == 1)
            co = 1;
        elseif (data(row+1,col) == 1 && data(row,col+1) == 1 && data(row-1,col-1) == 1)
            co = 1;
        elseif (data(row+1,col) == 1 && data(row,col-1) == 1 && data(row-1,col+1) == 1)
            co = 1;
        elseif (data(row-1,col) == 1 && data(row,col+1) == 1 && data(row+1,col-1) == 1)
            co = 1;
        elseif (data(row-1,col) == 1 && data(row,col-1) == 1 && data(row+1,col+1) == 1)
            co = 1;
        else
            co = 0;  %walker can stick here
        end
        
    else
        co = 0;  %walker can stick here
    end
end