import React, { useState } from 'react';
// 1. Importe o objeto 'toast' do react-toastify
import { toast } from 'react-toastify';

export default function JourneyForm() {
    const [stopCount, setStopCount] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();

        // Converte o valor para um número para a validação
        const count = parseInt(stopCount, 10);

        // --- LÓGICA DE VALIDAÇÃO ---
        // Verifica se o campo está vazio ou não é um número
        if (isNaN(count) || stopCount.trim() === '') {
            // 2. Dispara um toast de erro
            toast.error("Por favor, insira um número de paradas.");
            return; // Interrompe a execução
        }

        // Verifica se o valor está fora do intervalo permitido (1 a 12)
        if (count < 1 || count > 12) {
            // 3. Dispara um toast de erro específico
            toast.error("O número de paradas deve ser entre 1 e 12.");
            return; // Interrompe a execução
        }

        // --- SUCESSO ---
        // Se a validação passar, você pode opcionalmente mostrar um toast de sucesso
        toast.success(`Iniciando aventura com ${count} paradas!`);

        console.log("Formulário enviado com sucesso:", count);
        // Aqui você continuaria com a lógica de redirecionamento, etc.
    };

    return (
        <form onSubmit={handleSubmit} className="w-full max-w-lg mx-auto">
            <h1 className="text-center text-3xl font-bold font-mono mb-8">
                "Somos a Dot Win, a plataforma que transforma seu caminho em uma jornada de recompensas. Escolha sua rota, acumule pontos a cada parada e troque por prêmios incríveis. Sua próxima aventura começa aqui!"
            </h1>

            <div className="flex flex-col items-center justify-center my-4 gap-4">
                <div className="flex flex-col items-center gap-2">
                    <label htmlFor="qtdpontos" className="font-mono font-bold">
                        Insira a quantidade de pontos:
                    </label>
                    <input
                        id="qtdpontos"
                        name="qtdpontos"
                        type="number"
                        placeholder="Ex: 12"
                        className="w-80 h-10 border-2 border-black bg-white rounded-lg px-2 text-center placeholder:font-mono placeholder:italic"
                        // Remova min e max para que a validação do toast seja a principal
                        value={stopCount}
                        onChange={(e) => setStopCount(e.target.value)}
                    />
                </div>

                <button
                    type="submit"
                    className="bg-black text-white font-bold font-mono py-2 px-8 rounded-lg hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-500"
                >
                    Iniciar Aventura
                </button>
            </div>
        </form>
    );
}
