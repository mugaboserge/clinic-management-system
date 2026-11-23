function LibraryButton({ children, onClick }) {
    return (
        <button
            className="library-button"
            onClick={onClick}
        >
            {children}
        </button>
    );
}

export default LibraryButton;