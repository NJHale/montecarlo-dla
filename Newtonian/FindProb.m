function p = FindProb(data, row, col, A, B, l)
%prob = FindProb(row, col, A, B, l)
%data is the matrix of 0's and 1's
%row is the row where the walker might stick
%col is the column where the walker might stick
%A is specificed from the input, for the probability equation
%B is specificed from the input, for the probability equation
%l is specificed from the input, for the probability equation

    Ni = 0;
    for i = row-4:row+4            %using a lxl box count the number of 1s inside :kadanoff paper (http://m.njit.edu/~kondic/capstone/2014/vicsek_prl_84.pdf)
        for j = col-4:col+4
            if data(i,j) == 1  %if there is a walker already at that location, add one to Ni
                Ni = Ni+1;
            end
        end
    end
    
    C = .01;    
    p = A*((Ni/l^2)-((l-1)/(2*l)))+ B; %probability of sticking based on number of 1s inside box
    
                                            %A = 1 and B = 0.5
    
    if (p < C)                    %if the probability happens to be negative (small curvature) set p=C so the code doesnt get stuck
        p = C;                    %to get negatives, you need to make A and B different than 1 and 0.5
    end
end