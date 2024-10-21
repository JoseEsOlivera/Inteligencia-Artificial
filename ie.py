import numpy as np

# Función de activación: Signo
def sgn(x):
    return np.where(x >= 0, 1, -1)

# Definir la red de Hopfield
class HopfieldNetwork:
    def __init__(self, size):
        self.size = size
        self.weights = np.zeros((size, size))
    
    def train(self, patterns):
        # Entrenamiento usando la regla de Hebb
        for pattern in patterns:
            self.weights += np.outer(pattern, pattern)
        np.fill_diagonal(self.weights, 0)  # Evitar auto-conexiones
    
    def predict(self, input_pattern, steps=5):
        # Iterar para actualizar el estado de la red
        output = np.copy(input_pattern)
        for _ in range(steps):
            output = sgn(np.dot(self.weights, output))
        return output

# Crear un patrón simple de 10x10 (círculo o cuadrado, por ejemplo)
original_pattern = np.array([[-1, -1, 1, 1, 1, 1, 1, -1, -1, -1],
                             [-1, 1, 1, 1, 1, 1, 1, 1, -1, -1],
                             [1, 1, 1, 1, 1, 1, 1, 1, 1, -1],
                             [1, 1, 1, 1, 1, 1, 1, 1, 1, -1],
                             [1, 1, 1, 1, 1, 1, 1, 1, 1, -1],
                             [1, 1, 1, 1, 1, 1, 1, 1, 1, -1],
                             [-1, 1, 1, 1, 1, 1, 1, 1, -1, -1],
                             [-1, 1, 1, 1, 1, 1, 1, 1, -1, -1],
                             [-1, -1, 1, 1, 1, 1, 1, -1, -1, -1],
                             [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]])

# Añadir ruido (distorsión) al patrón
noisy_pattern = np.copy(original_pattern)
noisy_pattern[2, 2] = -1
noisy_pattern[5, 5] = -1

# Aplanar los patrones para que Hopfield trabaje con vectores 1D
original_pattern_flat = original_pattern.flatten()
noisy_pattern_flat = noisy_pattern.flatten()

# Inicializar y entrenar la red de Hopfield
hopfield_net = HopfieldNetwork(size=original_pattern_flat.size)
hopfield_net.train([original_pattern_flat])

# Recuperar el patrón original a partir del patrón ruidoso
output_pattern_flat = hopfield_net.predict(noisy_pattern_flat)
output_pattern = output_pattern_flat.reshape(original_pattern.shape)

# Imprimir los resultados
print("Patrón Original:")
print(original_pattern)
print("Patrón Ruidoso:")
print(noisy_pattern)
print("Patrón Recuperado:")
print(output_pattern)
