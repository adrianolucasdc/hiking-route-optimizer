import React, { useState } from 'react';
import { toast } from 'react-toastify';
import StopsMatrix from '../Matriz/StopsMatrix' // 1. Importe o novo componente

export default function JourneyForm() {
    const [stopCount, setStopCount] = useState('');
    const [submittedCount, setSubmittedCount] = useState(null);

    const handleSubmit = (event) => {
        event.preventDefault();
        const count = parseInt(stopCount, 10);

        if (isNaN(count) || stopCount.trim() === '') {
            toast.error("Por favor, insira um número de Pontos.");
            return;
        }

        if (count < 2 || count > 12) {
            toast.error("O número de Pontos deve ser entre 2 e 12.");
            return;
        }

        toast.success(`Iniciando configuração para ${count} Pontos!`);
        setSubmittedCount(count);
    };

    const handleBackToForm = () => {
        setSubmittedCount(null);
        setStopCount('');
        toast.info("Você pode inserir uma nova quantidade de Pontos.");
    };

    if (!submittedCount) {
        return (
            <form onSubmit={handleSubmit} className="w-full max-w-lg mx-auto">
                <h1 className="text-center text-3xl font-bold font-mono mb-8">
                    "Somos a Dot Win, a plataforma que transforma seu caminho em uma jornada de recompensas..."
                </h1>
                <div className="flex flex-col items-center justify-center my-4 gap-4">
                    <div className="flex flex-col items-center gap-2">
                        <label htmlFor="qtdpontos" className="font-mono font-bold">
                            Insira a quantidade de Pontos:
                        </label>
                        <input
                            id="qtdpontos"
                            name="qtdpontos"
                            type="number"
                            placeholder="Ex: 12"
                            className="w-80 h-10 border-2 border-black bg-white rounded-lg px-2 text-center placeholder:font-mono placeholder:italic"
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

    return <StopsMatrix count={submittedCount} onBack={handleBackToForm} />;
}