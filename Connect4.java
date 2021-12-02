package com.example.javgame;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javafx.stage.Stage;
import javafx.scene.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Connect4 extends Application
{
    static final int TILE_SIZE = 80;
    static final int COLUMNS = 7;
    static final int ROWS = 6;
    Pane discRoot = new Pane();
    Disc[][] grid = new Disc[COLUMNS][ROWS];
    boolean Player = true;

    public Parent createContent()
    {
        Pane root = new Pane();


        Shape gridShape = makeGrid();

        root.getChildren().add(gridShape);
        root.getChildren().add(discRoot);
        root.getChildren().addAll(makeColumns());
        return root;
    }
    Shape makeGrid()
    {
        Shape shape = new Rectangle((COLUMNS + 1) * TILE_SIZE, (ROWS + 1) * TILE_SIZE);
        //640x560

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                Circle circle = new Circle(TILE_SIZE / 2);
                circle.setCenterX(TILE_SIZE / 2);
                circle.setCenterY(TILE_SIZE / 2);
                circle.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 4);
                circle.setTranslateY(y * (TILE_SIZE + 5) + TILE_SIZE / 4);

                shape = Shape.subtract(shape, circle);
            }
        }
        shape.setFill(Color.BLUE);
        return shape;
    }

    List<Rectangle> makeColumns()
    {
        List<Rectangle> list = new ArrayList<>();

        for (int x = 0; x < COLUMNS; x++) {
            Rectangle rect = new Rectangle(TILE_SIZE, (ROWS + 1) * TILE_SIZE);
            rect.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 4);
            rect.setFill(Color.TRANSPARENT);

            EventHandler<MouseEvent> entry= new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent me)
                {
                    rect.setFill(Color.rgb(0, 0, 0, 0.25));
                }
            };
            EventHandler<MouseEvent> exit= new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent me)
                {
                    rect.setFill(Color.TRANSPARENT);
                }
            };
            rect.addEventHandler(MouseEvent.MOUSE_ENTERED,entry);
            rect.addEventHandler(MouseEvent.MOUSE_EXITED,exit);

            final int column = x;
            EventHandler<MouseEvent> event=new EventHandler<MouseEvent>()
            {
              public void handle(MouseEvent me)
              {
                  placeDisc(new Disc(Player), column);

              }
            };
            rect.addEventHandler(MouseEvent.MOUSE_CLICKED,event);

            list.add(rect);
        }

        return list;
    }

     static class Disc extends Circle
     {
        final boolean red;
        public Disc(boolean red)
        {
            super(TILE_SIZE / 2, red ? Color.RED : Color.YELLOW);
            this.red = red;

            setCenterX(TILE_SIZE / 2);
            setCenterY(TILE_SIZE / 2);
        }
    }

     void placeDisc(Disc disc, int column)
    {
        int row = ROWS - 1;
        do {
            if (!getDisc(column, row).isPresent())
                break;

            row--;
        } while (row >= 0);

        if (row < 0)
            return;

        grid[column][row] = disc;
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (TILE_SIZE + 5) + TILE_SIZE / 4);
        disc.setTranslateY(row * (TILE_SIZE + 5) + TILE_SIZE / 4);
        final int currentRow = row;
        if (gameEnded(column, currentRow))
        {
            gameOver();
            //System.exit(0);
        }
        Player = !Player;
    }
    void gameOver()
    {
        String s=(Player ? "RED" : "YELLOW");
        System.out.println("Winner: " + s);
        Rectangle rect=new Rectangle(140,220,360,110);
        rect.setFill(Color.BLUE);
        rect.setArcWidth(5);
        rect.setArcHeight(10);
        discRoot.getChildren().add(rect);
        Text text=new Text("Winner : "+s);
        text.setX(220);
        text.setY(290);
        text.setFont(Font.font("Britannic Bold",32));
        text.setFill(Color.WHITE);
        discRoot.getChildren().add(text);
        Button b1=new Button("Exit");
        EventHandler<MouseEvent> event=new EventHandler<MouseEvent>()
        {
          public void handle(MouseEvent me)
          {
            System.exit(0);
          }
        };
        b1.addEventHandler(MouseEvent.MOUSE_CLICKED,event);
        discRoot.getChildren().add(b1);

    }
    Optional<Disc> getDisc(int column, int row)
    {
        if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS)
            return Optional.empty();

        return Optional.ofNullable(grid[column][row]);
    }
    private boolean gameEnded(int column, int row) {
        int x,a,b;
        int vertical[][] = new int[ROWS][2];
        //from r-3 to r+3
        x = row - 3;
        if (x < 0)
            x = 0;
        for (int i = 0; i < ROWS; i++) {
            vertical[i][0] = column;
            vertical[i][1] = x;
            x++;
            if (x > row + 3)
                break;
        }

        int horizontal[][] = new int[COLUMNS][2];
        //from c-3 to c+3
        x = column - 3;
        if (x < 0)
            x = 0;
        for (int i = 0; i < COLUMNS; i++) {
            horizontal[i][0] = x;
            horizontal[i][1] = row;
            x++;
            if (x > column + 3)
                break;
        }

        int diagnol1[][] = new int[7][2];
        a=column-3;
        b=row-3;
        for(int i=0;i<7;i++)
        {
            diagnol1[i][0]=a+i;
            diagnol1[i][1]=b+i;
        }
        int diagnol2[][] = new int[7][2];
        a=column-3;
        b=row+3;
        for(int i=0;i<7;i++)
        {
            diagnol2[i][0]=a+i;
            diagnol2[i][1]=b-i;
        }
        return checkRange(vertical) || checkRange(horizontal) || checkRange(diagnol1) || checkRange(diagnol2);
    }
    boolean checkRange(int points[][])
    {
        int chain = 0;
        for (int i = 0; i < points.length; i++)
        {
            int column = points[i][0];
            int row = points[i][1];
            Disc disc = getDisc(column, row).orElse(new Disc(!Player));
            if (disc.red == Player)
            {
                chain++;
                if (chain == 4)
                    return true;
            } else
                chain = 0;
        }
        return false;
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Connect 4");
        stage.setScene(new Scene(createContent()));

        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}