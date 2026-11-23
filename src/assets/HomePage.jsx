import LibraryButton from "./LibraryButton";

function HomePage() {
    return (
        <section id="home">

            <h1>MUGABO SERGWEy</h1>

            <h2>MUGABO Serge</h2>

            <p>
                Welcome to our library.
                Discover and explore your favourite books.
            </p>

            <LibraryButton
                onClick={() => alert("Welcome to MyLibrary!")}
            >
                Explore Books
            </LibraryButton>

        </section>
    );
}

export default HomePage;