import Footer from "../../components/Footer";
import Header from "../../components/Header";
import JourneyForm from "../../components/Forms/JourneyForm";

export default function Home() {
    return (
        <div className="pt-34 pb-34">
            <Header />
            <main className="flex-grow flex items-center justify-center px-4">
                <JourneyForm />
            </main>
            <Footer />
        </div>
    )
}