//
//  labelAndText.swift
//  ios-itmo-2022-assignment2
//
//  Created by Артемий on 26.10.2022.
//


import UIKit
public class LabelAndText: UIView {
    public lazy var nameText: UITextField = {
        let text = UITextField()
        text.translatesAutoresizingMaskIntoConstraints = false
        text.backgroundColor = .systemGray6
        text.font = UIFont.systemFont(ofSize: 16)
        text.textAlignment = .left
        text.layer.cornerRadius = 8

        text.placeholder = "Введите название фильма"
        return text
    }()
    public lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = "Название"
        label.font = UIFont.systemFont(ofSize: 12)
        label.textAlignment = .left
        return label
    }()
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    func configureView(labelTitle: String, textPlaceholder: String) {
        nameLabel.text = labelTitle
        nameText.placeholder = textPlaceholder
    }
    private func setupView() {
        nameText.addTarget(self, action: #selector(enterPressed), for: .editingDidEndOnExit)
        addSubview(nameText)
        addSubview(nameLabel)
        translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([

            //nameText (Название фильма)
            nameText.topAnchor.constraint(equalTo: topAnchor, constant: 23),
            nameText.leadingAnchor.constraint(equalTo: leadingAnchor),
            nameText.trailingAnchor.constraint(equalTo: trailingAnchor),
            nameText.bottomAnchor.constraint(equalTo: bottomAnchor),

            //nameLabel (название)
            nameLabel.topAnchor.constraint(equalTo: topAnchor),
            nameLabel.leadingAnchor.constraint(equalTo: leadingAnchor),
            nameLabel.trailingAnchor.constraint(equalTo: trailingAnchor),
            nameLabel.bottomAnchor.constraint(equalTo: nameText.topAnchor, constant: -8)
            ])
    }
    @objc private func enterPressed() {
        nameText.resignFirstResponder()
    }

}
