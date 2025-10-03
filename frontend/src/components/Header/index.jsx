import logoPath from '../../assets/images/dot_win_logo.svg';

export default function Header() {
    return (
        <div className="fixed top-0 left-0 w-full flex justify-center">
            <header className="rounded-lg shadow-sm m-4 bg-black w-full">
                <div className="w-full mx-auto max-w-screen-xl p-4 flex justify-center items-center">
                    <a href="/" className="flex items-center gap-3">
                        <img src={logoPath} alt="Dot Win Logo" className=" text-white" />
                    </a>
                </div>
            </header>
        </div>
    );
}
