import React from 'react';

const getPointName = (index) => String.fromCharCode(65 + index);

export default function JourneyReport({ matrix, onBack }) {
    // Filtra e formata os dados para exibição, mostrando apenas conexões ativas
    const reportData = matrix.flatMap((row, rowIndex) =>
        row
            .map((cell, colIndex) => ({
                from: getPointName(rowIndex),
                to: getPointName(colIndex),
                value: cell.value,
                checked: cell.checked,
            }))
            .filter(item => item.checked) // Mostra apenas as conexões que foram marcadas
    );

    return (
        <div className="p-4 md:p-8 w-full max-w-2xl mx-auto">
            <h2 className="text-2xl font-bold font-mono text-center mb-6">
                Relatório da Jornada
            </h2>

            {reportData.length > 0 ? (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white border-2 border-black font-mono">
                        <thead className="bg-black text-white">
                            <tr>
                                <th className="p-3 border border-gray-300">De (Ponto)</th>
                                <th className="p-3 border border-gray-300">Para (Ponto)</th>
                                <th className="p-3 border border-gray-300">Valor</th>
                            </tr>
                        </thead>
                        <tbody>
                            {reportData.map((item, index) => (
                                <tr key={index} className="text-center">
                                    <td className="p-3 border border-gray-300 font-bold">
                                        {item.from}
                                    </td>
                                    <td className="p-3 border border-gray-300 font-bold">
                                        {item.to}
                                    </td>
                                    <td className="p-3 border border-gray-300">
                                        {item.value}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <p className="text-center font-mono text-gray-600">
                    Nenhuma conexão foi realizada nesta jornada.
                </p>
            )}

            <div className="text-center mt-8">
                <button
                    onClick={onBack}
                    className="bg-blue-600 text-white font-bold font-mono py-2 px-8 rounded-lg hover:bg-blue-700"
                >
                    Criar Nova Jornada
                </button>
            </div>
        </div>
    );
}
