CREATE TABLE utilizador (
    id uuid PRIMARY KEY,
    nome text NOT NULL CHECK (nome ~ '[^[:space:]]'),
    email text NOT NULL UNIQUE CHECK (email ~ '[^[:space:]]'),
    password_hash text NOT NULL CHECK (password_hash ~ '[^[:space:]]')
);

CREATE TABLE projeto (
    id uuid PRIMARY KEY,
    nome text NOT NULL CHECK (nome ~ '[^[:space:]]'),
    descricao text,
    arquivado boolean NOT NULL DEFAULT false,
    proprietario_id uuid NOT NULL REFERENCES utilizador(id)
);

CREATE TABLE tarefa (
    id uuid PRIMARY KEY,
    titulo text NOT NULL CHECK (titulo ~ '[^[:space:]]'),
    descricao text,
    estado text NOT NULL DEFAULT 'Por Fazer'
        CHECK (estado IN ('Por Fazer', 'Em Curso', 'Em Revisão', 'Concluída')),
    prioridade text CHECK (prioridade IN ('Baixa', 'Média', 'Alta', 'Crítica')),
    prazo date,
    projeto_id uuid REFERENCES projeto(id) ON DELETE CASCADE,
    criador_id uuid NOT NULL REFERENCES utilizador(id)
);

CREATE TABLE colaboracao_projeto (
    projeto_id uuid NOT NULL REFERENCES projeto(id) ON DELETE CASCADE,
    utilizador_id uuid NOT NULL REFERENCES utilizador(id),
    PRIMARY KEY (projeto_id, utilizador_id)
);

CREATE TABLE atribuicao_tarefa (
    tarefa_id uuid NOT NULL REFERENCES tarefa(id) ON DELETE CASCADE,
    utilizador_id uuid NOT NULL REFERENCES utilizador(id),
    PRIMARY KEY (tarefa_id, utilizador_id)
);

CREATE TABLE comentario (
    id uuid PRIMARY KEY,
    conteudo text NOT NULL CHECK (conteudo ~ '[^[:space:]]'),
    tarefa_id uuid NOT NULL REFERENCES tarefa(id) ON DELETE CASCADE,
    autor_id uuid NOT NULL REFERENCES utilizador(id),
    criado_em timestamptz NOT NULL
);

-- PostgreSQL indexes PK/UNIQUE automatically; FK lookup/cascade needs these.
CREATE INDEX ix_projeto_proprietario ON projeto(proprietario_id);
CREATE INDEX ix_tarefa_projeto ON tarefa(projeto_id);
CREATE INDEX ix_tarefa_criador ON tarefa(criador_id);
CREATE INDEX ix_colaboracao_utilizador ON colaboracao_projeto(utilizador_id);
CREATE INDEX ix_atribuicao_utilizador ON atribuicao_tarefa(utilizador_id);
CREATE INDEX ix_comentario_tarefa ON comentario(tarefa_id);
CREATE INDEX ix_comentario_autor ON comentario(autor_id);
