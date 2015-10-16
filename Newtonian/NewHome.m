function [row, col] = NewHome(row,col,data)
%[row, col] = NewHome(row,col,data)
%row is the row the walker wants to stick to
%col is the column the walker wants to stick to
%data is the matrix of 1's and 0's
%move is a number[0 3] corresponding tothe last direction the walker moved

%     row0 = row;
%     col0 = col;
%     max=0;
%     for i=row-1:row+1            %using a lxl box count the number of 1s inside :kadanoff paper (http://m.njit.edu/~kondic/capstone/2015/vicsek_prl_84.pdf)
%         for j=col-1:col+1           %move walker to place with least potential highest number of neighbors
%             if data(i,j,1)==0
%                 count=data(i+1,j)+data(i,j+1)+data(i+1,j+1)+data(i-1,j)+data(i,j-1)+data(i-1,j-1)+data(i-1,j+1)+data(i+1,j-1);
%                 if(max<count)
%                     max=count;
%                     row=i;
%                     col=j;
%                 end
%             end
%         end     
%     end

    max = sum(sum(data(row-1:row+1,col-1:col+1)));
    max0 = max;
    tiedForMax = [row, col];
    for i = row-1:row+1            %using a lxl box count the number of 1s inside :kadanoff paper (http://m.njit.edu/~kondic/capstone/2015/vicsek_prl_84.pdf)
        for j = col-1:col+1           %move walker to place with least potential highest number of neighbors
            if data(i,j,1)==0
                count = sum(sum(data(i-1:i+1,j-1:j+1)));
                if (count > max)
                    max = count;
                    tiedForMax = [i,j];
                elseif (max == count && count > max0)
                    tiedForMax = [tiedForMax; [i,j]];
                end
            end
        end     
    end
    
    nelm = size(tiedForMax,1);
    if (nelm == 1)
        row = tiedForMax(1, 1);
        col = tiedForMax(1, 2);
    else
        i = 1 + floor(rand()*nelm);
        row = tiedForMax(i, 1);
        col = tiedForMax(i, 2);
    end
    
    %%% THIS WAS ADDED INCASE NEWHOME MOVED THE WALKER WHICH WOULD CAUSE STOPDIAGONALHOLES FROM WORKING CORRECTLY 
%     DeltaRow = row-row0;
%     DeltaCol = col-col0;
%     if DeltaRow == 0 && DeltaCol ==0
%         move = move;
%     elseif DeltaRow == 0 && DeltaCol == 1
%         move = 2;   %walker was moved east
%     elseif DeltaRow == 0 && DeltaCol == -1
%         move = 3;   %walker was moved west
%     elseif DeltaRow == 1 && DeltaCol == 0
%         move = 0;   %walker was moved north
%     elseif DeltaRow == -1 && DeltaCol == 0
%         move = 1;   %walker was moved south
%     elseif DeltaRow == 1 && DeltaCol == 1
%         move = 2;
%     elseif DeltaRow == 1 && DeltaCol == -1
%         move = 3;
%     elseif DeltaRow == -1 && DeltaCol == 1
%         move = 2;
%     elseif DeltaRow == -1 && DeltaCol == -1
%         move = 3;  
%     end    
end