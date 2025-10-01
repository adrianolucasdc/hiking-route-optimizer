import { useCallback } from "react";
// import { Home } from "lucide-react"; // se não tiver, posso te mostrar versão sem essa lib

export default function FloatingHomeButton() {
  const handleReload = useCallback(() => {
    window.location.reload();
  }, []);

  return (
    <button
      onClick={handleReload}
      className="
        fixed bottom-6 right-6
        flex items-center justify-center
        w-14 h-14 rounded-full
        bg-blue-600 text-white shadow-lg
        hover:bg-blue-700 transition
      "
    >
      {/* <Home size={24} /> */}
    </button>
  );
}
