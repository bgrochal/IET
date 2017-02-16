#include <iostream>
#include <vector>
#include <stack>

using namespace std;

/** STRUCTURES. **/

struct Result {
    int fromRow;
    int fromColumn;

    int toRow;
    int toColumn;
};

struct Figure {
    int row;
    int column;
    long long int value;
};

struct Delta {
    int deltaRow;
    int deltaColumn;
};


/** FUNCTIONS' HEADERS. **/

bool isPathFound(int queensOnChessboard, vector<Figure> figuresQueue);
Figure findFigureToHit(int row, int column, int deltaRow, int deltaColumn);
void addToQueue(Figure figure, vector<Figure>* figuresQueue);
bool figureEquals(Figure f1, Figure f2);


/** GLOBAL COMPLEX TYPES DECLARATION. **/

stack<Result> resultsStack;
Delta directions[8] = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};


/** GLOBAL PRIMITIVE TYPES DECLARATION. **/

int N, K;
long long int** chessboard;


int main() {
    vector<Figure> figuresQueue;
    int queensOnChessboard = 0;

    /** Data reading. **/
    cin >> N >> K;

    chessboard = new long long int* [N];

    for(int i = 0; i < N; i++) {
        chessboard[i] = new long long int[N];

        for(int j = 0; j < N; j++) {
            cin >> chessboard[i][j];
            if(chessboard[i][j] != 0) {
                queensOnChessboard++;
                addToQueue({i, j, chessboard[i][j]}, &figuresQueue);
            }
        }
    }

    /** Main logic and printing result, if exists. **/
    if(isPathFound(queensOnChessboard, figuresQueue)) {
        while(!resultsStack.empty()) {
            Result resultItem = resultsStack.top();
            resultsStack.pop();

            cout << resultItem.fromRow << " " << resultItem.fromColumn << " " << resultItem.toRow << " " << resultItem.toColumn << endl;
        }
    }

    return 0;
}


/**
 * Returns true if there exists a path which gives requested result.
 * Recursive.
 */
bool isPathFound(int queensOnChessboard, vector<Figure> figuresQueue) {
    if(queensOnChessboard <= K) {
        return true;
    }

    while(!figuresQueue.empty()) {
        Figure currentFigure = figuresQueue.front();
        figuresQueue.erase(figuresQueue.begin());

        /** For each available direction to hit. **/
        for(int i = 0; i < 8; i++) {
            Figure figureToHit = findFigureToHit(currentFigure.row, currentFigure.column, directions[i].deltaRow, directions[i].deltaColumn);
            if(figureToHit.value == currentFigure.value) {
                vector<Figure>::iterator iterator;
                vector<Figure> newFiguresQueue;

                /** Creating new list with unprocessed positions. **/
                for(iterator = figuresQueue.begin(); iterator != figuresQueue.end(); iterator++) {
                    if(figureEquals(*iterator, figureToHit)) {
                        continue;
                    }

                    newFiguresQueue.push_back(*iterator);
                }

                /** Updating chessboard and list with unprocessed position with changes made by current hit. **/
                chessboard[currentFigure.row][currentFigure.column] = 0;
                chessboard[figureToHit.row][figureToHit.column] *= 2;
                Figure newFigure = {figureToHit.row, figureToHit.column, figureToHit.value*2};
                addToQueue(newFigure, &newFiguresQueue);

                /** Recursive call on chessboard "updated" by current hit. **/
                if(isPathFound(queensOnChessboard-1, newFiguresQueue)) {
                    resultsStack.push({currentFigure.row, currentFigure.column, figureToHit.row, figureToHit.column});
                    return true;
                }

                /** Restoring changes made by current hit after returning from recursive call. **/
                chessboard[currentFigure.row][currentFigure.column] = currentFigure.value;
                chessboard[figureToHit.row][figureToHit.column] = figureToHit.value;
            }
        }
    }

    return false;
}


/**
 * Returns a structure identifying the first figure on chessboard in given direction from figure with given coordinates.
 */
Figure findFigureToHit(int row, int column, int deltaRow, int deltaColumn) {
    column += deltaColumn;
    row += deltaRow;

    while(row >= 0 and row < N and column >= 0 and column < N) {
        if(chessboard[row][column] != 0) {
            return {row, column, chessboard[row][column]};
        }

        column += deltaColumn;
        row += deltaRow;
    }

    return {-1, -1, 0};
}


/**
 * Adds new element to sorted priority queue implemented as list.
 */
void addToQueue(Figure figure, vector<Figure>* figuresQueue) {
    vector<Figure>::iterator iterator;

    for(iterator = figuresQueue->begin(); iterator != figuresQueue->end(); iterator++) {
        if((*iterator).value > figure.value) {
            figuresQueue->insert(iterator, figure);
            return;
        }
    }

    figuresQueue->push_back(figure);
}


/**
 * Returns true if two figures are equals, e.g. if their positions are the same.
 */
bool figureEquals(Figure f1, Figure f2) {
    return f1.row == f2.row && f1.column == f2.column;
}
