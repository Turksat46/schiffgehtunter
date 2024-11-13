import numpy as np
import tensorflow as tf
import random

print("Num GPUs Available: ", len(tf.config.list_physical_devices('GPU')))

class BattleshipGame:
    def __init__(self, board_size):
        self.board_size = board_size
        self.board = [['~' for _ in range(board_size)] for _ in range(board_size)]
        self.ships = []

    def place_ship(self, ship_size):
        placed = False
        while not placed:
            orientation = random.choice(['horizontal', 'vertical'])
            if orientation == 'horizontal':
                row = random.randint(0, self.board_size - 1)
                col = random.randint(0, self.board_size - ship_size)
                if all(self.board[row][col + i] == '~' for i in range(ship_size)):
                    for i in range(ship_size):
                        self.board[row][col + i] = 'S'
                    placed = True
            else:
                row = random.randint(0, self.board_size - ship_size)
                col = random.randint(0, self.board_size - 1)
                if all(self.board[row + i][col] == '~' for i in range(ship_size)):
                    for i in range(ship_size):
                        self.board[row + i][col] = 'S'
                    placed = True
        self.ships.append((row, col, ship_size, orientation))

    def next_move(self):
        while True:
            row = random.randint(0, self.board_size - 1)
            col = random.randint(0, self.board_size - 1)
            if self.board[row][col] == '~' or self.board[row][col] == 'S':
                return row, col

def create_model(input_shape):
    with tf.device('/GPU:0'):
        model = tf.keras.Sequential([
            tf.keras.layers.Flatten(input_shape=input_shape),
            tf.keras.layers.Dense(64, activation='relu'),
            tf.keras.layers.Dense(2)
            # Output: x, y coordinates
    ])
    model.compile(optimizer='adam', loss='mean_squared_error')
    return model

def create_training_data(num_samples, max_size):
    x_train = []
    y_train = []
    # Erstellen von zufälligen Spielfeldern und Zügen
    for _ in range(num_samples):
        game = BattleshipGame(max_size)
        game.place_ship(random.randint(1, 5))
        next_x, next_y = game.next_move()
        board = np.array(game.board)
        board[board == '~'] = 0
        board[board == 'S'] = 1
        x_train.append(board.astype(np.float32).flatten())
        y_train.append([next_x, next_y])
    return np.array(x_train), np.array(y_train)

max_board_size = 30
num_samples = 100000
epochs = 300

x_train, y_train = create_training_data(num_samples, max_board_size)

model = create_model((max_board_size * max_board_size,))
model.fit(x_train, y_train, epochs=epochs)

new_board = np.array([[0,0,1],
                    [0,0,0],
                    [0,0,0]])

# Auffüllen des neuen Boards auf die erwartete Größe
padded_board = np.zeros((max_board_size, max_board_size))
padded_board[:new_board.shape[0], :new_board.shape[1]] = new_board

prediction = model.predict(np.array([padded_board.flatten()]))
print(prediction)
model.save('model.h5')