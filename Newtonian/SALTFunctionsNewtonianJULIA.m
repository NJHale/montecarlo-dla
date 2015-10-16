function [Num_walk dimension] = SALTFunctionsNewtonianJULIA(n,num_part,A,B,l)
%data = SALTFunctionsNewtonianJULIA(n,num_part,A,B,l)
%n = the size of the rectangle that holds the circle
%num_part= the amount of walkers you want, normally 10000
%A = variable for probability, normally 1
%B = variable for probability, normally 0.5
%l = varible used to find local curvature, normally 9

    %Muy Importante!!!!!!!  If this is not commented out, then every time you run the code, it will have the same randomness, and make the same shape!!!
    s = RandStream('mcg16807','seed', 8);      %to change to a different set randomness, change the last digit to something else 
%     RandStream.setGlobalStream(s);            %comment this out too if want totally randomly different each time

    f = @(x,row) -2*x+row+1; %north and south: f=row+1 for south, f=row-1 for north  || when a number is randomly generated, this function changes it into the correct movement: N or S
    g = @(x,col) -2*x+col+5; %west and east: g=col+1 for east g=col-1 for west  || when a number is randomly generated, this function changes it into the correct movement: W or E

    fig = figure('Visible', 'off');  %starts the figure
    xo = floor(n/2); %center of circle (really finds the center of the square determined by the n value given to the function)
    yo = floor(n/2); %center of circle
    hold on;  %allowing the points to plot during the 'for' loop
    
%     plot(rectangle('Position',[xo,yo,1,1],'FaceColor','m')); %makes the rectangles on the plot so the results can be seen with greater ease
    rectangle('Position',[xo,yo,1,1],'FaceColor','m');
    drawnow;    %making it so you can see as the walkers stick

    data = zeros(n,n,'int8');  %initializing data
    data(xo,yo,1) = 1; %making the first walker stick to the center
    rsq = @(x,y) (x-xo)^2+(y-yo)^2;   %making the initial circle around the seed, using the equation for a circle

    R = floor((n/50)); %set Radius where random walkers spawn to 1/10th of matrix dimension n || floor to round down to whole integer
    
    dimension=[];
    Num_walk = 0;
    for k = 1:num_part;  %runs the function for the amount of walkers initally specified 
        
        moveon = 0;
        
        if mod(k, 50) == 0   %prints the k value every 50 times so it does not slow down the code as much
            k
        end
        theta =(2*pi)*rand; %modeled as a circle in first quadrant centered at (xo,yo) || rand randomly generates a number so each walker in theory will start from a different spot
        col = floor(xo+(R-1)*cos(theta)); %start the walker at a random position along the circle of radius R using the randomly generated theta value and the R value
        row = floor(yo+(R-1)*sin(theta));

        columnflag = 0;  %used to allow for a new walker to start walking
        while (columnflag == 0)

            move = randi([0 3],1,1); % 0 north 1 south 2 east 3 west, begin moving the walker randomly
                                   %returns a one-by-one matrix with a value of 0,1,2, or 3 which is later put into the g or f 
                                   %function above to determine which direction the walker is told to move
            occupiedflag = 0;
            if(move >= 2) %if walker is to be moved east or west (IE move= 2 or 3) check whether that "move" is ok to make (not equal to 1)
                if((rsq(row,g(move,col))< R^2) && (data(row, g(move,col))~= 1)) %make sure that move is not going to cross over our boundary determined by our R (Radius) value
                    col = g(move,col); %make move if the walker will not move out of the circle
                else
                    col = col; %if move not okay, then another randomly generated move is found
                end
                
                neigh = data(row,col+1) + data(row, col-1) + data(row-1,col) + data(row+1,col); %looking to see if the walker is next to a previously stuck walker
                
                if neigh >= 1
                    occupiedflag = 1; %if the walker is next to a stuck walker, then the 'if occupiedflag == 1' loop is begun and the walker has a chance of sticking there
                end

            else %if the move is south or north (0 or 1) check whether the move will be occupied
                if((rsq(f(move,row),col) < R^2)&&(data(f(move,row),col) ~= 1)) %make sure the move is not going to cross the circle determined by the Radius
                    row = f(move,row); %make move
                else
                    row = row; %if move not okay, then another randomly generated move is found
                end
                
                neigh = data(row,col+1) + data(row, col-1) + data(row-1,col) + data(row+1,col); %looking to see if the walker is next to a previously stuck walker
                
                if neigh >= 1
                    occupiedflag = 1;  %if the walker is next to a stuck walker, then the 'if occupiedflag == 1' loop is begun and the walker has a chance of sticking there
                end
            end
                        
            if occupiedflag == 1 %we have reached the seed-- need to check the local curvature near the seed               
                p = FindProb(data, row, col, A, B, l); %function used to determine the probability for the walker to stick where it is (takes in data, row, col, A, B, and l)(outputs p)
                if(rand() <= p || p >= 1)         % roll a dice (rand) and determine if walker sticks OR ( the '||' in the code ) if the proability is > 1 (high curvature) stick
                                                % if rand is greater than p, then that 'if' loop is broken, the occupied flag loop is broken, this walker will not stick and a new walker is begun

%                     [row, col] = NewHome(row,col,data);%function using a lxl box to count the number of 1s inside :kadanoff paper (http://m.njit.edu/~kondic/capstone/2014/vicsek_prl_84.pdf)(takes in row, col, and data)(returns col and row which might or might not have been updated)
                    row0 = row;
                    col0 = col;
                    while (1==1)
                        [row, col] = NewHome(row,col,data);%function using a lxl box to count the number of 1s inside :kadanoff paper (http://m.njit.edu/~kondic/capstone/2014/vicsek_prl_84.pdf)(takes in row, col, and data)(returns col and row which might or might not have been updated)
                        if (row == row0 && col == col0)
                            break;
                        end
                        row0 = row;
                        col0 = col;
                    end
                    
                    co = holePreventTJ(row, col, data); %function made to prevent holes using many conditionals (inputs row, col, and data)(outputs a value co, which is used below to determine whether the walker is allowed to stick or not)                 

                    if co == 0; %the walker is finally allowed to stick
                        Num_walk = Num_walk +1;
                        columnflag = 1; % to break you out of the while loop so a new walker can start after this 'if' loop is finished    
                        data(row,col,1)=1; %walker has stuck (all spots where a walker has stuck have a value of '1', all other spots are '0')
%                         plot(rectangle('Position',[row,col,1,1],'FaceColor','m')); %plots that walker on the plot
                        rectangle('Position',[row,col,1,1],'FaceColor','m');
                        drawnow;
                        title('Newtonian Fluid'); %adds the title to the graph
                        xlabel('x axis');  %adds the x axis label to the graph
                        ylabel('y axis');  %adds the y axis label to the graph
                        axis([xo-R xo+R yo-R yo+R]);  % makes the axises change according to how large the current Radius is (centered around the first walker)
                        if (rsq(row,col)>(R-5)^2) %adjust R based on last stick, make sure that the last sticking particle is within 2 of R
                            R = R + floor(n/100); %makes R bigger ck
                        end
                    else 
                        moveon = moveon + 1;
                    end   
                    
                    if moveon >= 10
                        columnflag = 1;  %does not allow that walker to stick because it got stuck
                    end
                end  
            end
        end
        if mod(k,500)==0, 
            dimension(k/500,1)=k; 
            dimension(k/500,2)=hausDim(data);
        end
    end
    figure();
    plot(dimension(:,1),dimension(:,2),'-o');
    title('Fractal Dimension as a Function of Time SEED=8');
    xlabel('Time (# of Walkers)'); ylabel('Fractal Dimension');
    figure();
    plot(dimension(2:end,2)./dimension(1:(end-1),2),'-o');
    title('Density Correlation as a Function of Time');

    filename = sprintf('SALTFunctionsNewtonianJULIA(%d, %d, %g, %g, %g).fig', n, num_part, A, B, l);
    saveas(fig, filename);
    set(fig, 'visible', 'on');
end