function [ D ] = hausDim( I )
% HAUSDIM Returns the Haussdorf fractal dimension of an object represented by
% a binary image.

    % Pad the image with background pixels so that its dimensions are a power of 2.
    maxDim = max(size(I));
    newDimSize = 2^ceil(log2(maxDim));
    rowPad = newDimSize - size(I, 1);
    colPad = newDimSize - size(I, 2);
    I = padarray(I, [rowPad, colPad], 'post');

    boxCounts = zeros(1, ceil(log2(maxDim)));
    resolutions = zeros(1, ceil(log2(maxDim)));
    
    boxSize = size(I, 1);
    boxesPerDim = 1;
    idx = 0;
    while boxSize >= 1
        boxCount = 0;
        
        for boxRow = 1:boxesPerDim
            for boxCol = 1:boxesPerDim
                minRow = (boxRow - 1) * boxSize + 1;
                maxRow = boxRow * boxSize;
                minCol = (boxCol - 1) * boxSize + 1;
                maxCol = boxCol * boxSize;
                
                objFound = false;
                for row = minRow:maxRow
                    for col = minCol:maxCol
                        if I(row, col)
                            boxCount = boxCount + 1;
                            objFound = true; % Break from nested loop.
                        end;
                        
                        if objFound
                            break; % Break from nested loop.
                        end;
                    end;
                    
                    if objFound
                        break; % Break from nested loop.
                    end;
                end;
            end;
        end;
        
        idx = idx + 1;
        boxCounts(idx) = boxCount;
        resolutions(idx) = 1 / boxSize;
        
        boxesPerDim = boxesPerDim * 2;
        boxSize = boxSize / 2;
    end;
    
    D = polyfit(log(resolutions), log(boxCounts), 1);
    D = D(1);
    end